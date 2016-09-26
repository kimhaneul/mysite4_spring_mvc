package kr.ac.sungkyul.mysite.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import kr.ac.sungkyul.mysite.vo.BoardVo;

@Repository
public class BoardDao {

	private Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			conn = DriverManager.getConnection(url, "webdb", "webdb");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return conn;
	}

	public void insert(BoardVo vo) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		System.out.println(vo);
		try {
			conn = getConnection();

			System.out.println("group " + vo.getGroup_no());

			if (String.valueOf(vo.getGroup_no()).equals("0")) {
				System.out.println("새글 달기");

				String sql = "insert into board values (seg_board.nextval, ?, ?,sysdate,?, seg_group.nextval,1,1,?)";
				// 글 번호, 제목, 내용, 날짜, 조회수, 그룹번호, 그룹내순서, 글깊이, 회원번호

				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, vo.getTitle());
				pstmt.setString(2, vo.getContent());
				pstmt.setInt(3, 0);// 조회수 view_count
				pstmt.setLong(4, vo.getUser_no());

			} else {
				System.out.println("답글 달기");

				String sql2 = "update board set order_no= order_no+1 where group_no = ? and order_no > ?";
				pstmt = conn.prepareStatement(sql2);
				pstmt.setLong(1, vo.getGroup_no());
				pstmt.setLong(2, vo.getOrder_no());
				pstmt.executeUpdate();

				String sql = "insert into board values (seg_board.nextval, ?, ?,sysdate,?,?,?,?,?)";
				// 글 번호, 제목, 내용, 날짜, 조회수, 그룹번호, 그룹내순서, 글깊이, 회원번호
				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, vo.getTitle());
				pstmt.setString(2, vo.getContent());
				pstmt.setInt(3, 0);// 조회수 view_count
				pstmt.setLong(4, vo.getGroup_no());// 그룹번호 group_no
				pstmt.setLong(5, vo.getOrder_no() + 1L);
				pstmt.setLong(6, vo.getDepth());// 글깊이 depth
				pstmt.setLong(7, vo.getUser_no());

			}

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<BoardVo> getList(int pageNum, int pageCount) {
		List<BoardVo> list = new ArrayList<BoardVo>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			String sql = "SELECT * FROM ( SELECT A.*, ROWNUM AS RNUM FROM "
					+ "(select no, title, (select name from users where no = user_no) as user_name, view_count, reg_date, user_no, depth from board  order by group_no desc, order_no asc) A ) "
					+ "where (?-1)*?+1 <= RNUM AND RNUM <= ?*?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, pageNum);
			pstmt.setInt(2, pageCount);
			pstmt.setInt(3, pageNum);
			pstmt.setInt(4, pageCount);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				Long no = rs.getLong(1);
				String title = rs.getString(2);
				String user_name = rs.getString(3);
				int view_count = rs.getInt(4);
				String reg_date = rs.getString(5);
				Long user_no = rs.getLong(6);
				Long depth = rs.getLong(7);

				BoardVo vo = new BoardVo();

				vo.setNo(no);
				vo.setTitle(title);
				vo.setUser_name(user_name);
				vo.setView_count(view_count);
				vo.setReg_date(reg_date);
				vo.setUser_no(user_no);
				vo.setDepth(depth);

				list.add(vo);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	
	public int get_Ccontent_Count() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int content_Count = 0;
		BoardVo vo = new BoardVo();
		try {
			conn = getConnection();

			String sql = "select count ( * ) from board";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				content_Count = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return content_Count;
	}


	public BoardVo modify(BoardVo vo) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = getConnection();

			String sql = "update board set title = ?, content = ? where no=?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, vo.getTitle());
			pstmt.setString(2, vo.getContent());
			pstmt.setLong(3, vo.getNo());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vo;
	}

	public void delete(Long no) {

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = getConnection();

			String sql = "delete from board where no = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, no);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public BoardVo view(Long no) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		BoardVo vo = new BoardVo();
		try {
			conn = getConnection();

			String sql = "select no, title, content, group_no, order_no, depth from board where no =" + no;
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Long no1 = rs.getLong(1);
				String title = rs.getString(2);
				String content = rs.getString(3);
				Long group_no = rs.getLong(4);
				Long order_no = rs.getLong(5);
				Long depth = rs.getLong(6);

				vo.setNo(no1);
				vo.setTitle(title);
				vo.setContent(content);
				vo.setGroup_no(group_no);
				vo.setOrder_no(order_no);
				vo.setDepth(depth);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return vo;
	}

	public void updateViewCount(Long no) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();

			String sql = "update board set view_count = view_count + 1 where no = ?";
			pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, no);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
