package seattlerHub.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import seattlerHub.model.*;

public class SchoolsDao {
	protected ConnectionManager connectionManager;

	private static SchoolsDao instance = null;
	protected SchoolsDao() {
		connectionManager = new ConnectionManager();
	}

	public static SchoolsDao getInstance() {
		if(instance == null) {
			instance = new SchoolsDao();
		}
		return instance;
	}

	
	public int getNumOfSchoolsWithinCertainMilesOfHousing(Housing housing, int radius) throws SQLException {
		String selectSchools = "SELECT COUNT(*) AS NumOfSchools FROM Schools "
				+ "WHERE Longitude != 0 AND Latitude != 0 AND (SQRT(POWER(69.1 * (Latitude - ?), 2) "
				+ "+ POWER(69.1 * (? - Longitude ) * COS(Latitude / 57.3), 2))) <= ?;";
		
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectSchools);
			selectStmt.setDouble(1, housing.getLatitude());
			selectStmt.setDouble(2, housing.getLongitude());
			selectStmt.setInt(3, radius);
			results = selectStmt.executeQuery();
			
			if(results.next()) {
				int numOfSchools = results.getInt("NumOfSchools");
				return numOfSchools;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStmt != null) {
				selectStmt.close();
			}
			if(results != null) {
				results.close();
			}
		}
		return -1;
		
	}

	public int getNumOfCertainTypeSchoolsWithinCertainMilesOfHousing(Housing housing, int radius, Schools.SchoolType schoolType) throws SQLException {
		String selectSchools = "SELECT COUNT(*) AS NumOfSchools FROM Schools "
				+ "WHERE Longitude != 0 AND Latitude != 0 AND (SQRT(POWER(69.1 * (Latitude - ?), 2) "
				+ "+ POWER(69.1 * (? - Longitude ) * COS(Latitude / 57.3), 2))) <= ? "
				+ "AND Schools.Type = 'Private';";
		
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectSchools);
			selectStmt.setDouble(1, housing.getLatitude());
			selectStmt.setDouble(2, housing.getLongitude());
			selectStmt.setInt(3, radius);
			selectStmt.setString(4, schoolType.name());
			results = selectStmt.executeQuery();
			
			if(results.next()) {
				int numOfSchools = results.getInt("NumOfSchools");
				return numOfSchools;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStmt != null) {
				selectStmt.close();
			}
			if(results != null) {
				results.close();
			}
		}
		return -1;
	}
	
	public int getNumOfSchoolsByNeighborhood(Neighborhoods neighborhood) throws SQLException {
		String selectSchools = "SELECT COUNT(*) AS NumOfSchools FROM Schools WHERE Zipcode = ?;";
		
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectSchools);
			selectStmt.setInt(1, neighborhood.getZipcode());
			results = selectStmt.executeQuery();
			
			if(results.next()) {
				int numOfSchools = results.getInt("NumOfSchools");
				return numOfSchools;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStmt != null) {
				selectStmt.close();
			}
			if(results != null) {
				results.close();
			}
		}
		return -1;
	}
	
	public int getNumOfCertainTypeSchoolsByNeighborhood(Neighborhoods neighborhood,Schools.SchoolType schoolType) throws SQLException {
		String selectSchools = "SELECT COUNT(*) AS NumOfSchools FROM Schools WHERE Zipcode = ? AND Schools.Type = 'Private';";
		
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectSchools);
			selectStmt.setInt(1, neighborhood.getZipcode());
			selectStmt.setString(2, schoolType.name());
			results = selectStmt.executeQuery();
			
			if(results.next()) {
				int numOfSchools = results.getInt("NumOfSchools");
				return numOfSchools;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStmt != null) {
				selectStmt.close();
			}
			if(results != null) {
				results.close();
			}
		}
		return -1;
	}
	
	
	
	public List<Housing> getTop20HousingsWithMostSchools() throws SQLException {
		List<Housing> housings = new ArrayList<>();
		String schoolDistance = "(SQRT(POWER(69.1 * (Schools.Latitude - Housing.Latitude), 2) "
				+ "+ POWER(69.1 * ( Housing.Longitude - Schools.Longitude ) * COS(Schools.Latitude / 57.3), 2))) <= 1 ";
		String selectHousings = "SELECT HousingId, COUNT(*) AS NumNearbySchools FROM Housing CROSS JOIN Schools "
				+ "WHERE Schools.Longitude != 0 AND Schools.Latitude != 0 AND " 
				+ schoolDistance
				+ "GROUP BY HousingId "
				+ "ORDER BY NumNearbySchools DESC LIMIT 20";
	
		Connection connection = null;
		PreparedStatement selectStmt = null;
		ResultSet results = null;
		
		HousingDao housingDao = HousingDao.getInstance();
		
		try {
			connection = connectionManager.getConnection();
			selectStmt = connection.prepareStatement(selectHousings);
			results = selectStmt.executeQuery();
			
			while(results.next()) {
				int housingId = results.getInt("HousingId");
				Housing housing = housingDao.getHousingByHousingId(housingId);
				housings.add(housing);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(connection != null) {
				connection.close();
			}
			if(selectStmt != null) {
				selectStmt.close();
			}
			if(results != null) {
				results.close();
			}
		}
		return housings;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
