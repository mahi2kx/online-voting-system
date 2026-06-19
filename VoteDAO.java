package voting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VoteDAO.java
 * -------------
 * Data Access Object (DAO) for the "votes" table.
 * Handles casting votes, duplicate-vote detection, and results aggregation.
 *
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class VoteDAO {

    // Shared database connection
    private final Connection conn;

    /** Constructor – obtains a connection from the DBConnection utility. */
    public VoteDAO() {
        this.conn = DBConnection.getConnection();
    }

    // =========================================================================
    // 1. CAST VOTE
    // =========================================================================

    /**
     * Records a vote for the given voter → candidate pair.
     * The database UNIQUE constraint on voter_id prevents duplicates at the
     * storage level; we also check programmatically for a friendlier message.
     *
     * @param  vote  Vote object containing voterId and candidateId
     * @return true  if the vote was recorded; false if already voted or error
     */
    public boolean castVote(Vote vote) {
        // Programmatic duplicate check for user-friendly feedback
        if (hasVoted(vote.getVoterId())) {
            System.out.println("[!] You have already cast your vote. Duplicate voting is not allowed.");
            return false;
        }

        String sql = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vote.getVoterId());
            ps.setInt(2, vote.getCandidateId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            // Handle the DB-level UNIQUE violation as well
            if (e.getErrorCode() == 1062) { // MySQL: Duplicate entry
                System.out.println("[!] Duplicate vote detected at database level.");
            } else {
                System.err.println("[DB ERROR] castVote: " + e.getMessage());
            }
            return false;
        }
    }

    // =========================================================================
    // 2. CHECK IF VOTER HAS ALREADY VOTED
    // =========================================================================

    /**
     * Returns true if a vote record already exists for the given voter id.
     *
     * @param  voterId  the voter's database id
     * @return boolean
     */
    public boolean hasVoted(int voterId) {
        String sql = "SELECT id FROM votes WHERE voter_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, voterId);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true = a vote record exists
        } catch (SQLException e) {
            System.err.println("[DB ERROR] hasVoted: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // 3. GET ELECTION RESULTS
    // =========================================================================

    /**
     * Aggregates vote counts per candidate and returns them in a Map.
     * The map is ordered by vote count descending (highest votes first).
     *
     * Returns: Map<CandidateName_Party, voteCount>
     *
     * @return LinkedHashMap preserving insertion (sorted) order
     */
    public Map<String, Integer> getResults() {
        // LinkedHashMap retains the ORDER BY order from the SQL result
        Map<String, Integer> results = new LinkedHashMap<>();

        String sql =
            "SELECT c.name AS cname, c.party, COUNT(v.id) AS total " +
            "FROM candidates c " +
            "LEFT JOIN votes v ON c.id = v.candidate_id " +
            "GROUP BY c.id, c.name, c.party " +
            "ORDER BY total DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String key   = rs.getString("cname") + " (" + rs.getString("party") + ")";
                int    votes = rs.getInt("total");
                results.put(key, votes);
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] getResults: " + e.getMessage());
        }
        return results;
    }

    // =========================================================================
    // 4. TOTAL VOTES CAST
    // =========================================================================

    /**
     * Returns the total number of votes recorded in the votes table.
     *
     * @return int  total vote count
     */
    public int getTotalVotes() {
        String sql = "SELECT COUNT(*) AS total FROM votes";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] getTotalVotes: " + e.getMessage());
        }
        return 0;
    }
}
