package voting;

/**
 * Candidate.java
 * ---------------
 * Model class (entity) representing an election candidate.
 * Encapsulates: id, name, and party affiliation.
 *
 * Follows OOP encapsulation – all fields are private,
 * accessed/mutated through public getters and setters.
 */
public class Candidate {

    // ── Fields ────────────────────────────────────────────────────────────
    private int    id;
    private String name;
    private String party;

    // ── Constructors ──────────────────────────────────────────────────────

    /** Default (no-arg) constructor. */
    public Candidate() {}

    /**
     * Constructor for creating a new candidate before persisting
     * (id assigned by the database on insert).
     */
    public Candidate(String name, String party) {
        this.name  = name;
        this.party = party;
    }

    /**
     * Full constructor used when reading a candidate back from the database.
     */
    public Candidate(int id, String name, String party) {
        this.id    = id;
        this.name  = name;
        this.party = party;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getParty() { return party; }

    // ── Setters ───────────────────────────────────────────────────────────

    public void setId(int id)          { this.id    = id; }
    public void setName(String name)   { this.name  = name; }
    public void setParty(String party) { this.party = party; }

    // ── toString ──────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("  [%d] %-25s | Party: %s", id, name, party);
    }
}
