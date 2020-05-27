import java.util.ArrayList;
import java.util.HashSet;

public class Rule {
    // HashMap or arrayList of terms?

    boolean implication;
    boolean literal;

    //KB 1: implication rule
    ArrayList<Predicate> predicateList = new ArrayList<Predicate>();

    //KB 2: if single literal
    Predicate predicateConstant;

    public Rule(String s,int index){
        if(s.contains("=")){
            implication = true;
            literal = false;
        }
        else{
            literal = true;
            implication = false;
        }
        if(implication) {
            int impIndex = s.indexOf('=');
            String precedent = s.substring(0,impIndex-1);
            String consequent = s.substring(impIndex+3);
            String[] preTermsList = precedent.split(" ");
            Predicate predicate;
            for(int i=0;i<preTermsList.length;i++){

                if(preTermsList[i].equals("&")){
                    continue;
                }

                String term = preTermsList[i];
                if(term.startsWith("~")){
                    term = term.substring(1);
                    predicate = new Predicate(term,index);
                }
                else{
                    predicate =  new Predicate("~"+preTermsList[i],index);
                }
                // Add predicate to HashSet or ArrayList
                this.predicateList.add(predicate);
            }
            // Add consequent
            this.predicateList.add(new Predicate(consequent,index));

        }

        else if(literal){
            this.predicateList.add(new Predicate(s,index));
            this.predicateConstant = new Predicate(s,index);
        }
    }


    public Rule(ArrayList<Predicate> listTerms){
        this.predicateList = listTerms;
    }

    public Rule(){

    }

    //ToDo: override equals and hash code

}
