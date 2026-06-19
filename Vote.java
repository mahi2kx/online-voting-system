package voting;

/**
 * Vote.java
 * ----------
 * Model class (entity) representing a single vote cast in the election.
 * Links a voter (voter_id) to a candidate (candidate_id).
 *
 * The database enforces a UNIQUE constraint on voter_id in the votes
 * table so that each voter can cast exactly one vote.
 */
public class Vote {

    // ── Fields ────────────────────────────────────────────────────────────
    private int id;
    private int voterId;
    private int candidateId;

    // ── Constructors ──────────────────────────────────────────────────────

    /** Default (no-arg) constructor. */
    public Vote() {}

    /**
     * Constructor used when recording a new vote
     * (id is assigned by the database on insert).
     */
    public Vote(int voterId, int candidateId) {
        this.voterId      = voterId;
        this.candidateId  = candidateId;
    }

    /**
     * Full constructor used when reading a vote back from the database.
     */
    public Vote(int id, int voterId, int candidateId) {
        this.id           = id;
        this.voterId      = voterId;
        this.candidateId  = candidateId;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public int getId()          { return id; }
    public int getVoterId()     { return voterId; }
    public int getCandidateId() { return candidateId; }

    // ── Setters ───────────────────────────────────────────────────────────

    public void setId(int id)                  { this.id          = id; }
    public void setVoterId(int voterId)        { this.voterId     = voterId; }
    public void setCandidateId(int candidateId){ this.candidateId = candidateId; }

    // ── toString ──────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Vote{id=" + id + ", voterId=" + voterId + ", candidateId=" + candidateId + "}";
    }
}
