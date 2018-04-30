package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "catch_species")
public class CatchSpecies extends EntityWithId {

    //Species from https://www.gov.uk/government/publications/buyers-and-sellers-of-first-sale-fish-and-submission-of-sales-notes/list-of-common-species-codes-for-fish-landed-in-the-united-kingdom
//    private static final String[][] SPECIES = {
//            {"Anglerfishes nei", "ANF", "Lophiidae"},
//            {"Atlantic cod", "COD", "Gadus morhua"},
//            {"Atlantic halibut", "HAL", "Hippoglossus hippoglossus"},
//            {"Atlantic herring", "HER", "Clupea harengus"},
//            {"Atlantic horse mackerel", "HOM", "Trachurus trachurus"},
//            {"Atlantic mackerel", "MAC", "Scomber scombrus"},
//            {"Ballan wrasse", "USB", "Labrus bergylta"},
//            {"Black seabreamv", "BRB", "Spondyliosoma cantharus"},
//            {"Blonde ray", "RJH", "Raja brachyura"},
//            {"Brill", "BLL", "Scophthalmus rhombus"},
//            {"Catsharks, nursehounds nei", "SCL", "Scyliorhinus spp"},
//            {"Clams, etc. nei", "CLX", "Bivalvia"},
//            {"Common cuttlefish", "CTC", "Sepia officinalis"},
//            {"Common dab", "DAB", "Limanda limanda"},
//            {"Common edible cockle", "COC", "Cerastoderma edule"},
//            {"Common octopus", "OCC", "Octopus vulgaris"},
//            {"Common prawn", "CPR", "vPalaemon serratus"},
//            {"Common shrimp", "CSH", "Crangon crangon"},
//            {"Common sole", "SOL", "Solea solea"},
//            {"Cuckoo ray", "RJN", "Raja naevus"},
//            {"Cuttlefish, bobtail squids nei", "CTL", "Sepiidae, Sepiolidae"},
//            {"Dogfishes and hounds nei", "DGH", "Squalidae, Scyliorhinidae"},
//            {"Edible crab", "CRE", "Cancer pagurus"},
//            {"European anchovy", "ANE", "Engraulis encrasicolus"},
//            {"European conger", "COE", "Conger conger"},
//            {"European flat oyster", "OYF", "Ostrea edulis"},
//            {"European flounder", "FLE", "Platichthys flesus"},
//            {"European hake", "HKE", "Merluccius merluccius"},
//            {"European lobster", "LBE", "Homarus gammarus"},
//            {"European pilchard(=Sardine)", "PIL", "Sardina pilchardus"},
//            {"European plaice", "PLE", "Pleuronectes platessa"},
//            {"European seabass", "BSS", "Dicentrarchus labrax"},
//            {"European smelt", "SME", "Osmerus eperlanus"},
//            {"European sprat", "SPR", "Sprattus sprattus"},
//            {"European squid", "SQR", "Loligo vulgaris"},
//            {"Garfish", "GAR", "Belone belone"},
//            {"Gilthead seabream", "SBG", "Sparus aurata"},
//            {"Great Atlantic scallop", "SCE", "Pecten maximus"},
//            {"Green crab", "CRG", "Carcinus maenas"},
//            {"Grey gurnard", "GUG", "Eutrigla gurnardus"},
//            {"Haddock", "HAD", "Melanogrammus aeglefinus"},
//            {"John dory", "JOD", "Zeus faber"},
//            {"Lemon sole", "LEM", "Microstomus kitt"},
//            {"Ling", "LIN", "Molva molva"},
//            {"Lumpfish(=Lumpsucker)", "LUM", "Cyclopterus lumpus"},
//            {"Manila clam", "CMM", "Corbicula manilensis"},
//            {"Megrim", "MEG", "Lepidorhombus whiffiagonis"},
//            {"Megrims nei", "LEZ", "Lepidorhombus spp"},
//            {"Mullets nei", "MUL", "Mugilidae"},
//            {"Norway lobster", "NEP", "Nephrops norvegicus"},
//            {"Pacific cupped oyster", "OYG", "Crassostrea gigas"},
//            {"Periwinkles nei", "PER", "Littorina spp"},
//            {"Pollack", "POL", "Pollachius pollachius"},
//            {"Pouting(=Bib)", "BIB", "Trisopterus luscus"},
//            {"Queen scallop", "QSC", "Aequipecten opercularis"},
//            {"Rabbit fish", "CMO", "Chimaera monstrosa"},
//            {"Red gurnard", "GUR", "Aspitrigla cuculus"},
//            {"Saithe(Coalfish)", "POK", "Pollachius virens"},
//            {"Sand sole", "SOS", "Solea lascaris"},
//            {"Sandeels(=Sandlances) nei", "SAN", "Ammodytes spp"},
//            {"Sea trout", "TRS", "Salmo trutta"},
//            {"Shortfin squids nei", "ILL", "Illex spp"},
//            {"Small-eyed ray", "RJE", "Raja microocellata"},
//            {"Small-spotted catshark", "SYC", "Scyliorhinus canicula"},
//            {"Smooth-hound", "SMD", "Mustelus mustelus"},
//            {"Solen razor clams nei", "RAZ", "Solen spp"},
//            {"Spinous spider crab", "SCR", "Maja squinado"},
//            {"Spotted ray", "RJM", "Raja montagui"},
//            {"Starry smooth-hound", "SDS", "Mustelus asterias"},
//            {"Thornback ray", "RJC", "Raja clavata"},
//            {"Tope shark", "GAG", "Galeorhinus galeus"},
//            {"Tub gurnard", "GUU", "Chelidonichthys lucerna"},
//            {"Turbot", "TUR", "Psetta maxima"},
//            {"Undulate ray", "RJU", "Raja undulata"},
//            {"Velvet swimming crab", "LIO", "Necora puber"},
//            {"Whelk", "WHE", "Buccinum undatum"},
//            {"Whiting", "WHG", "Merlangius merlangus"},
//    };

    //Species from FISH1 Form
    private static final String[][] SPECIES = {
            {"Ballan wrasse", "USB", null},
            {"Brill", "BLL", null},
            {"Brown Crab", "CRE", null},
            {"Cockles", "COC", null},
            {"Cod", "COD", null},
            {"Corkwing Wrasse", "YFM", null},
            {"Crawfish", "CRA", null},
            {"Cuckoo wrasse", "USI", null},
            {"Eel Conger", "COE", null},
            {"Goldsinny Wrasse", "TBR", null},
            {"Green Crab", "CRG", null},
            {"Gurnards", "GUR", null},
            {"Haddock", "HAD", null},
            {"Hake", "HKE", null},
            {"Halibut", "HAL", null},
            {"Herring", "HER", null},
            {"King Scallop", "SCE", null},
            {"Lemon Sole", "LEM", null},
            {"Ling", "LIN", null},
            {"Lobster", "LBE", null},
            {"Mackerel", "MAC", null},
            {"Megrim", "MEG", null},
            {"Monkfish", "MON", null},
            {"Nephrops", "NEP", null},
            {"Plaice", "PLA", null},
            {"Pollock", "POL", null},
            {"Queen Scallop", "QSC", null},
            {"Razorfish (Ensis)", "RAZ", null},
            {"Saithe", "POK", null},
            {"Shrimp", "PRX", null},
            {"Skate Common", "RJB", null},
            {"Skate Cuckoo", "RJN", null},
            {"Skate Spotted", "RJN", null},
            {"Skate Starry Ray", "RJR", null},
            {"Skate Thornback", "RJC", null},
            {"Skate White", "RJA", null},
            {"Sprats", "SPR", null},
            {"Squat Lobster", "LBS", null},
            {"Squid", "SQU", null},
            {"Turbot", "TUR", null},
            {"Velvet Crab", "CRS", null},
            {"Whelks", "WHE", null},
            {"Whiting", "WHG", null},
            {"Witches", "WIT", null}
    };

    @ColumnInfo(name = "species_name")
    public String speciesName;

    @ColumnInfo(name = "species_code")
    public String speciesCode;

    @ColumnInfo(name = "scientific_name")
    public String scientificName;

    public static List<CatchSpecies> createSpecies() {
        List<CatchSpecies> speciesObjects = new ArrayList();
        for(String[] speciesDetails : SPECIES) speciesObjects.add(new CatchSpecies(speciesDetails));
        return speciesObjects;
    }

    @Ignore
    public CatchSpecies(String[] speciesDetails) {
        this.setSpeciesName(speciesDetails[0]);
        this.setSpeciesCode(speciesDetails[1]);
        this.setScientificName(speciesDetails[2]);
    }

    public CatchSpecies(){}

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String toString() {
        return this.getSpeciesName() + " (" + this.getSpeciesCode() + ")";
    }
}
