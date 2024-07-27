package telran.employees.net;

import java.util.Arrays;
import java.util.Iterator;

import telran.employees.Company;
import telran.employees.Employee;
import telran.employees.Manager;
import telran.net.Request;
import telran.net.TcpClient;

public class CompanyProxy implements Company {
    TcpClient tcpClient;
     
    public CompanyProxy(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public Iterator<Employee> iterator() {
        throw new UnsupportedOperationException("Iterator is not supported for TCP proxy");
    }

    @Override
    public void addEmployee(Employee empl) {
        tcpClient.sendAndReceive(new Request("addEmployee", empl.getJSON()));
    }

    @Override
    public Employee getEmployee(long id) {
        String employeeJSON = tcpClient.sendAndReceive(new Request("getEmployee", String.valueOf(id)));
        Employee employee = null;
        if (employeeJSON == null || employeeJSON.isEmpty()) {
            employee = null;
        } else {
            employee = (Employee) new Employee().setObject(employeeJSON);
        }
        return employee;
    }

    @Override
    public Employee removeEmployee(long id) {
        String removedEmployeeJSON = tcpClient.sendAndReceive(new Request("removeEmployee", String.valueOf(id)));
        Employee removedEmployee = null;
        if (removedEmployeeJSON == null || removedEmployeeJSON.isEmpty()) {
            removedEmployee = null;
        } else {
            removedEmployee = (Employee) new Employee().setObject(removedEmployeeJSON);
        }
        return removedEmployee;
    }

    @Override
    public int getDepartmentBudget(String department) {
        String budgetString = tcpClient.sendAndReceive(new Request("getDepartmentBudget", department));
        int budget = Integer.parseInt(budgetString);
        return budget;
    }

    @Override
    public String[] getDepartments() {
        String departmentsJSON = tcpClient.sendAndReceive(new Request("getDepartments", ""));
        String[] departments = departmentsJSON.split(",");
        return departments;
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        String managersJSON = tcpClient.sendAndReceive(new Request("getManagersWithMostFactor", ""));
        Manager[] managers = Arrays.stream(managersJSON.split(";"))
                .map(s -> (Manager) new Employee().setObject(s))
                .toArray(Manager[]::new);
        return managers;
    }
}
