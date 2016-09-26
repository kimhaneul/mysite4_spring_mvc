package kr.ac.sungkyul.mysite.dao;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.ac.sungkyul.mysite.exception.UserInfoUpdateException;
import kr.ac.sungkyul.mysite.vo.UserVo;

@Repository
public class UserDao {

	@Autowired // == new로 객체 생성하는것과 비슷
	private DataSource dataSource;

	@Autowired // == new로 객체 생성하는것과 비슷
	private SqlSession sqlSession;

	// 커넥션을 사용하기 때문에
	// conn = getConnection(); 을 conn = dataSource.getConnection();으로 대체
	// private Connection getConnection() throws SQLException {
	// Connection conn = null;
	// try {
	// Class.forName("oracle.jdbc.driver.OracleDriver");
	// String url = "jdbc:oracle:thin:@localhost:1521:xe";
	// conn = DriverManager.getConnection(url, "webdb", "webdb");
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// return conn;
	// }

	public UserVo get(Long userNo) throws UserInfoUpdateException {

		UserVo vo = sqlSession.selectOne("user.getByNo", userNo);
		return vo;
	}

	public UserVo get(String email, String password) {
		UserVo userVo = new UserVo();
		userVo.setEmail(email);
		userVo.setPassword(password);

		UserVo vo = sqlSession.selectOne("user.getByEmailAndPassword", userVo);
		return vo;
	}

	public UserVo get(String email) {

		UserVo vo = sqlSession.selectOne("user.getByEmail", email);
		return vo;
	}

	public void insert(UserVo vo) {

		System.out.println(vo);

		int count = sqlSession.insert("user.insert", vo);
		System.out.println("수정된 개수 : " + count);
	}

	public void update(UserVo vo) {
		System.out.println("dao " + vo);

		sqlSession.update("user.update", vo);
	}
	
}
