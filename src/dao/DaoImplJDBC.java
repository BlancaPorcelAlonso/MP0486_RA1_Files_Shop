package dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {
		String baseUrl = "jdbc:mysql://localhost:3306";
		String user = "root";
		String pass = "";

		try {
			// 1) crear la base de datos si no existe
			try (Connection setup = DriverManager.getConnection(baseUrl, user, pass);
					Statement stmt = setup.createStatement()) {
				stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS shop");
			}

			// 2) conectar a ella y crear las tablas
			connection = DriverManager.getConnection(baseUrl + "/shop", user, pass);
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS employee (" + "employeeId INT PRIMARY KEY, "
						+ "name VARCHAR(100), " + "password VARCHAR(100))");
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS product (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "name VARCHAR(100), " + "price DOUBLE, " + "stock INT)");
			}
		} catch (SQLException e) {
			// que el error real salga a la luz en vez de un NPE más adelante
			throw new RuntimeException("No se pudo conectar a la base de datos", e);
		}
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, employeeId);
			ps.setString(2, password);
			// System.out.println(ps.toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
				}
			}
		} catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public boolean insertEmployee(Employee employee) {
		boolean success = false;
		String query = "insert into employee (employeeId, name, password) values (?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, employee.getEmployeeId());
			ps.setString(2, employee.getName());
			ps.setString(3, employee.getPassword());

			int rows = ps.executeUpdate();
			success = rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public ArrayList<Product> getInventory() {
		return null;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		return false;
	}

}
