package telran.employees.net;
import telran.employees.Employee;
import telran.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CompanyRobot {
	static AtomicInteger employeeId = new AtomicInteger(1000000);
	static int N_THREADS = 10;
	static int N_RUNS = 10000;

	public static void main(String[] args) {
		TcpClient [] clients = new TcpClient[N_THREADS];
		startClients(clients);
		

	}

	private static void startClients(TcpClient[] clients) {
		for (int i = 0; i < clients.length; i++) {
			clients[i] = new TcpClient("localhost", 5000);
			TcpClient client = clients[i];
			new Thread(() -> {
				for(int j = 0; j < N_RUNS; j++) {
					client.sendAndReceive(new Request("addEmployee",
							new Employee(employeeId.getAndIncrement(),
									10000, "Dep").getJSON()));
				}
				client.close();
			}).start();
		}
		
	}

}