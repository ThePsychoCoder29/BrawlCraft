package net.mrmisc.brawlcraft.util.helpers;

public class Constants {
    public static final String DYNAMIKE = "Dynamike";
    public static final String MAX = "Max";
    public static final String BEA = "Bea";
    public static final String JACKY = "Jacky";
    public static final String CROW = "Crow";
    public static final String DOUG = "Doug";
    public static final String BO = "Bo";
    public static final String NORMAL = "Normal";
    public static final String NO_BRAWLER = "No Brawler Selected !";

    public enum Brawlers {
        NORMAL(Constants.NORMAL ,1),
        DYNAMIKE(Constants.DYNAMIKE, 2),
        MAX(Constants.MAX, 3),
        BEA(Constants.BEA, 4),
        JACKY(Constants.JACKY, 5),
        CROW(Constants.CROW, 6),
        DOUG(Constants.DOUG, 7),
        BO(Constants.BO, 8);

        private final String name;
        private final int id;

        Brawlers(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static Brawlers fromID(int id){
            for(Brawlers brawlers : values()){
                if(brawlers.id == id){
                    return brawlers;
                }
            }
            return NORMAL;
        }

        public static Brawlers fromName(String name) {
            for (Brawlers brawlers : values()) {
                if (brawlers.name.equals(name)) {
                    return brawlers;
                }
            }
            return NORMAL;
        }
    }
}
