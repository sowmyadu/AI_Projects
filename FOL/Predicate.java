import java.util.ArrayList;

public class Predicate {

    String predicate;
    ArrayList<String> args;
    boolean negation;
    boolean toResolve;

    public Predicate(String term, int index) {
        this.args = new ArrayList<String>();
        if(term.contains("~")) {
            this.negation = true;
            //removing negation
            term = term.substring(1);
        }

        String argsList = "";
        this.predicate = term.substring(0,term.indexOf('('));
        argsList = term.substring(term.indexOf('(')+1,term.indexOf(')'));
        String args_arr[] = argsList.split(",");
        for(int i =0;i<args_arr.length;i++) {
            args_arr[i] = args_arr[i].trim();
            if(Character.isLowerCase(args_arr[i].charAt(0))){
                String standardized_arg = args_arr[i]+(index+1); //check if string is appending properly
                this.args.add(standardized_arg); // check if variables are getting standardized and not constants
            }
            else
                this.args.add(args_arr[i]);
        }

    }

    public Predicate(ArrayList<String> list, boolean negation, String predicate){
        this.args = list;
        this.negation = negation;
        this.predicate = predicate;
    }


    //ToDo: override equals and hash code


}

