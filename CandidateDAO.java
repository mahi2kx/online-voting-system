package voting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CandidateDAO.java
 * ------------------
 * Data Access Object (DAO) for the "candidates" table.
 * Handles adding candidates and retrieving the candidate list.
 *
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class CandidateDAO {

    // Shared database connection
    private final Connection conn;

    /** Constructor – obtains a connection from the DBConnection utility. */
    public CandidateDAO() {
        this.conn = DBConnection.getConnection();
    }

    // =========================================================================
    // 1. ADD CANDIDATE
    // =========================================================================

    /**
     * Inserts a new candidate record into the database.
     *
     * @param  candidate  Candidate object containing name and party
     * @return true        if the insert succeeded; false otherwise
     */
    public boolean addCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidates (name, party) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getParty());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[DB ERROR] addCandidate: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // 2. VIEW ALL CANDIDATES
    // =========================================================================

    /**
     * Fetches all candidates from the database.
     *
     * @return List of Candidate objects (empty if table is empty)
     */
    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                candidates.add(new Candidate(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("party")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] getAllCandidates: " + e.getMessage());
        }
        return candidates;
    }

    // =========================================================================
    // 3. FIND CANDIDATE BY ID
    // =========================================================================

    /**
     * Looks up a single candidate by their primary-key id.
     *
     * @param  id  the candidate's database id
     * @return Candidate if found; null otherwise
     */
    public Candidate getCandidateById(int id) {
        String sql = "SELECT * FROM candidates WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Candidate(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("party")
                );
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] getCandidateById: " + e.getMessage());
        }
        return null; // Candidate not found
    }
}
