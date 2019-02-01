package fr.epsi.book.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.epsi.book.domain.Contact;
import fr.epsi.book.domain.Contact.Type;

public class ContactDAO implements IDAO<Contact, Long> {

	private static final String INSERT_QUERY = "INSERT INTO contact (name, email, phone, type_var, type_num) values (?,?,?,?,?)";
	private static final String FIND_BY_ID_QUERY = "Select * from contact where id=";
	private static final String FIND_ALL_QUERY = "Select * from contact";
	private static final String UPDATE_QUERY = "UPDATE contact SET id=";
	private static final String REMOVE_QUERY = "DELETE FROM contact WHERE id=";

	@Override
	public void create(Contact c) throws SQLException {

		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
		st.setString(1, c.getName());
		st.setString(2, c.getEmail());
		st.setString(3, c.getPhone());
		st.setString(4, c.getType().getValue());
		st.setInt(5, c.getType().ordinal());
		st.executeUpdate();
		ResultSet rs = st.getGeneratedKeys();

		if (rs.next()) {
			c.setId(rs.getString(1));
		}
	}

	@Override
	public Contact findById(Long aLong) {
		String SELECT_USER_BY_ID_QUERY = null;
		Contact contact = null;

		SELECT_USER_BY_ID_QUERY = FIND_BY_ID_QUERY + aLong;
		ResultSet dataReturn = null;

		try (Connection connect = PersistenceManager.getConnection()) {
			PreparedStatement request = connect.prepareStatement(SELECT_USER_BY_ID_QUERY);
			Type typeReturn = null;
			dataReturn = request.executeQuery();
			
			if("perso" == dataReturn.getString("type")) {
				typeReturn = Type.PERSO;
			}else {
				typeReturn = Type.PRO;
			}
			
			contact = new Contact(dataReturn.getString("id"), dataReturn.getString("name"), dataReturn.getString("email"), dataReturn.getString("phone"), Type.PRO);
			connect.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contact;

	}

	@Override
	public List<Contact> findAll() {
		ResultSet dataReturn = null;
		List<Contact> contactList = new ArrayList<>();
		
		try (Connection connect = PersistenceManager.getConnection()){
			PreparedStatement request = connect.prepareStatement(FIND_ALL_QUERY);
			Type typeReturn = null;
			dataReturn = request.executeQuery();
			
			if("perso" == dataReturn.getString("type")) {
				typeReturn = Type.PERSO;
			}else {
				typeReturn = Type.PRO;
			}
			
			while(dataReturn.next()) {
				Contact contact = new Contact(dataReturn.getString("id"), dataReturn.getString("name"), dataReturn.getString("email"), dataReturn.getString("phone"), Type.PRO);
				contactList.add(contact);
			}
			
			connect.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return contactList;
	}

	@Override
	public Contact update(Contact o) {
		
		try (Connection connect = PersistenceManager.getConnection()){

			String updateQuery = FIND_ALL_QUERY + o.getId() + ", name=" + o.getName() + ", email=" + o.getEmail() + ", phone=" + o.getPhone() + ", type=" + o.getType() + "WHERE id=" + o.getId();
			
			PreparedStatement request = connect.prepareStatement(updateQuery);
			
			request.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void remove(Contact o) {
		try (Connection connect = PersistenceManager.getConnection()){
			
			String deleteQuery = REMOVE_QUERY + o.getId();
			PreparedStatement request = connect.prepareStatement(deleteQuery);
			
			request.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
