package vrpsim.resultdatastore.api;

public enum PersistenceUnit {
	
	LAPTOP("laptop"),
	DESKTOP("desktop"),
	DESKTOP_NEW("desktop_new"),
	SERVER("server");
	
	private final String text;

    /**
     * @param text
     */
    private PersistenceUnit(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
	
}
