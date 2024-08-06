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
		throw new UnsupportedOperationException("Iterator is not supported fro TCP proxy");
	}

	@Override
	public void addEmployee(Employee empl) {
		tcpClient.sendAndReceive(new Request("addEmployee", empl.getJSON()));

	}

	@Override
	public Employee getEmployee(long id) {
		return getRemoveEmployee("getEmployee",id);
	}

	private Employee getRemoveEmployee(String requestType, long id) {
		String emplJSON = tcpClient.sendAndReceive
				(new Request("getEmployee", "" + id));
		return (Employee) new Employee().setObject(emplJSON);
	}

	@Override
	public Employee removeEmployee(long id) {
		return getRemoveEmployee("removeEmployee", id);
	}

	@Override
	public int getDepartmentBudget(String department) {
		
		return Integer.parseInt(tcpClient
				.sendAndReceive(new Request("getDepartmentBudget", department)));
	}

	@Override
	public String[] getDepartments() {
		
		return tcpClient.sendAndReceive(new Request("getDepartments", ""))
				.split(";");
	}

	@Override
	public Manager[] getManagersWithMostFactor() {
		String managersJSON = tcpClient.sendAndReceive(new Request("getManagersWithMostFactor", ""));
		
		return Arrays.stream(managersJSON.split(";"))
				.map(s -> (Manager)new Employee().setObject(s))
				.toArray(Manager[]::new);
	}

}