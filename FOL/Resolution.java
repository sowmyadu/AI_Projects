import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Resolution {

        ArrayList<String> queries_string = new ArrayList<String>();
        ArrayList<String> KB_string = new ArrayList<String>();
        ArrayList<Rule> KB = new ArrayList<Rule>();
        ArrayList<Rule> KB_clone = new ArrayList<Rule>();

        public Resolution(ArrayList<String> query, ArrayList<String> knowledgeBaseString){
            this.queries_string = query;
            this.KB_string = knowledgeBaseString;
        }

        public void convertToCNF(){
            for(int i=0;i<this.KB_string.size();i++){
                Rule rl = new Rule(this.KB_string.get(i),i);
                KB.add(rl);
                KB_clone.add(rl);
//                for(int l=0;l<KB.get(i).predicateList.size();l++)
//                    System.out.println(KB.get(i).predicateList.get(l).args.get(0));
            }
        }

        //how to handle new sentences standardization

        public boolean evaluateQuery(String q){
            ArrayList<Rule> newFacts = new ArrayList<Rule>();

            Predicate predQuery = new Predicate(q,0);
            // check if query already in KB
            for(int i=0;i<KB_clone.size();i++){
                if(KB_clone.get(i).literal){
                    if(predicateEquals(predQuery,KB_clone.get(i).predicateConstant))
                        return true;
                }
            }

            // Negate query and add it to KB:
            Rule query;
            if(q.charAt(0)=='~')
                query = new Rule(q.substring(1),0);
            else
                query = new Rule("~"+q, 0);

            KB_clone.add(query);
            //System.out.println("Before calling resolve: "+KB_clone.size());

            boolean[] evaluated = new boolean[KB_clone.size()];

            int counter = 0;
            boolean res= resolve(query,evaluated,counter);

            KB_clone.remove(query);
            //System.out.println("After calling resolve: "+KB_clone.size());
            return res;
            /*while(true){
                int prevSizeOfPrevRunList = 1;
                boolean addedSentence = false;
                for(int i=0;i<KB_clone.size();i++){
                    if(i==0)
                        prevSizeOfPrevRunList = prevIteration.size();
                    ArrayList<Rule> temp =  new ArrayList<Rule>();
                    for(int j=0;j<prevIteration.size();j++){
                        temp.addAll(resolution(KB_clone[i],prevIteration[j]));
                    }
                    for(Rule r:temp){
                        if(r.predicateList.isEmpty()){
                            return true;
                        }
                        else{

                        }
                    }

                }
            }*/

            //Rule resolvedSentence = resolve(query);
        }

        public boolean resolve(Rule query,boolean[] evaluated, int counter){

            //find all facts in KB with predicate as negation of query
            //find facts with negation of atleast one of the predicates in resolved sentence

            // check if query already in KB

            // new boolean array for repeating in KB

            boolean taut_result = false;
            if(query.predicateList.size() == 2) {
                taut_result = tautology(query);
                if (taut_result) {
                    return true;
                }
            }

            boolean[] evaluated_temp = new boolean[KB_clone.size()];
            for(int i=0;i<KB_clone.size();i++){
                evaluated_temp[i] = evaluated[i];
            }
            //System.out.println(counter);

            HashMap<String,String> theta;
            Rule result = null;
            for(int i=0;i<KB_clone.size();i++){
                for(Predicate pred:KB_clone.get(i).predicateList){
                    theta = new HashMap<String,String>();

                    for(Predicate queryPredicate:query.predicateList){
                        if((pred.predicate.equals(queryPredicate.predicate)) && (pred.negation !=queryPredicate.negation)&& (evaluated[i] == false)){
                            if(theta == null)
                                theta = new HashMap<String,String>();
                            theta = unifyPredicate(theta,pred,queryPredicate);
                            //entailment(KB_clone.get(i),query,queryPredicate);
                            if(theta!=null && !theta.isEmpty()){
                                if(KB_clone.get(i).implication)
                                    evaluated[i] = true;

                                pred.toResolve = true;
                                queryPredicate.toResolve = true;
                                result = substitute(theta,KB_clone.get(i),query,queryPredicate,true);
                                pred.toResolve = false;
                                queryPredicate.toResolve = false;

                                if(result == null) {
                                    return true;
                                }
                                if(result!=null) {

                                    // for printing
                                    /*for(Predicate p:result.predicateList){
                                        System.out.print(p.negation+"->"+p.predicate+": ");
                                        for(String s:p.args){
                                            System.out.print(s);
                                        }
                                        System.out.println();
                                    }*/

                                    int ctr = counter++;
                                    boolean resolve_result = resolve(result,evaluated,++counter);
                                    if(resolve_result)
                                        return resolve_result;
                                    else {

                                        for(int b =0;b<evaluated.length;b++) {
                                            evaluated[b] = evaluated_temp[b];
                                        }

                                        continue;
                                    }
                                }
                            }
                            else {
                                //System.out.println("In else part(Theta=null)");
                                int cnt = 0;
                                for (int k = 0; k < pred.args.size(); k++) {
                                    if (pred.args.get(k).equals(queryPredicate.args.get(k)))
                                        cnt++;
                                }
                                if(cnt == pred.args.size()) {
                                    if(KB_clone.get(i).implication)
                                        evaluated[i] = true;
                                    pred.toResolve = true;
                                    queryPredicate.toResolve = true;
                                    result = substitute(theta, KB_clone.get(i), query, queryPredicate, false);

                                    pred.toResolve = false;
                                    queryPredicate.toResolve = false;

                                    if(result == null) {
                                        return true;
                                    }
                                    if(result!=null) {
                                        // for printing
                                        /*for(Predicate p:result.predicateList){
                                            System.out.print(p.negation+" Tnull->"+p.predicate+": ");
                                            for(String s:p.args){
                                                System.out.print(s);
                                            }
                                            System.out.println();
                                        }*/

                                        boolean resolve_result = resolve(result,evaluated,++counter);
                                        if(resolve_result) {
                                            return resolve_result;
                                        }
                                        else {
                                            // handle tautology
                                            for(int b =0;b<evaluated.length;b++) {
                                                evaluated[b] = evaluated_temp[b];
                                            }
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return false;


            // call recursive resolution with new resolved sentence
        }

        public boolean tautology(Rule result){
            boolean removed[] = new boolean[result.predicateList.size()];
            ArrayList<Predicate> termsList = result.predicateList;
            boolean taut = false;
            ArrayList<Predicate> resultTerms = new ArrayList<Predicate>();
            for (int i = 0; i < termsList.size(); i++) {
                for (int j = 0; j < termsList.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    if (termsList.get(i).predicate.equals(termsList.get(j).predicate) && termsList.get(i).negation != termsList.get(j).negation) {
                        if (allVariables(termsList.get(i)) && resultTerms.size()>2 && !removed[i]) {
                            resultTerms.remove(termsList.get(i));
                            removed[i] = true;
                            removed[j] = true;
                        } else if (allVariables(termsList.get(j)) && resultTerms.size()>2 && !removed[j]) {
                            resultTerms.remove(termsList.get(i));
                            removed[j] = true;
                            removed[i] = true;
                        }
                    }
                }
            }
            int cnt = 0;
            for (int i = 0; i < termsList.size(); i++) {
                if(removed[i] == true){
                    cnt++;
                }
            }
            if(cnt == termsList.size()){
                return true;
            }
            return taut;
        }

//        public void entailment(Rule KBfact,Rule resolvedFact,Predicate toResolve){
//            //Apply unification
//            HashMap<String,String> theta = new HashMap<String,String>();
//            for(Predicate factTerm:KBfact.predicateList){
//                for(Predicate resolvedTerm:resolvedFact.predicateList){
//                    // substitute unified variables
//                    if(theta!=null && !theta.isEmpty()){
//                       // substitute(theta,factTerm,resolvedTerm,toResolve);
//
//                    }
//                    else{
//                        if(factTerm.args.equals(resolvedTerm.predicate)){
//                            // call substitute for else part
//                        }
//                    }
//                }
//            }
//
//        }

        // substitute, resolve sentence
        public Rule substitute(HashMap<String,String> theta, Rule fact, Rule resolved,Predicate toResolve, boolean flag) {
            Rule resolvedFact;
            Rule result;
            ArrayList<Predicate> listTerms = new ArrayList<Predicate>();
            if (flag) {
                for (Predicate factTerm : fact.predicateList) {
                    if((factTerm.predicate.equals(toResolve.predicate)) && (!factTerm.toResolve)) {
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            if (theta.containsKey(s)) {
                                list.add(theta.get(s));
                            } else {
                                list.add(s);
                            }
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                    else if (!factTerm.predicate.equals(toResolve.predicate)) {
                   // if(!predicateEqualsWithoutNegation(factTerm,toResolve)){
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            if (theta.containsKey(s)) {
                                list.add(theta.get(s));
                            } else {
                                list.add(s);
                            }
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                }

                for (Predicate factTerm : resolved.predicateList) {
                    if((factTerm.predicate.equals(toResolve.predicate)) && (!factTerm.toResolve)){
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            //System.out.println("Args: "+s);
                            if (theta.containsKey(s)) {
                                list.add(theta.get(s));
                            } else {
                                list.add(s);
                            }
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                    else if (!factTerm.predicate.equals(toResolve.predicate)){
                        // if (!factTerm.predicate.equals(toResolve.predicate)) {
                   // if(!predicateEqualsWithoutNegation(factTerm,toResolve)){
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            //System.out.println("Args: "+s);
                            if (theta.containsKey(s)) {
                                list.add(theta.get(s));
                            } else {
                                list.add(s);
                            }
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                }


                if(listTerms.size() == 0)
                    return null;
                resolvedFact = new Rule(listTerms);
                // factorize to remove duplicate predicates
                resolvedFact = factorize(resolvedFact);
                /*for(Predicate pred:resolvedFact.predicateList)
                {
                    for(String arg: pred.args)
                        System.out.println(arg+"::predArgs::"+pred.negation);
                }*/
            }

            // for else part:
            else {
                for (Predicate factTerm : fact.predicateList) {
                    if((factTerm.predicate.equals(toResolve.predicate)) && (!factTerm.toResolve)) {
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            list.add(s);
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                    else if (!factTerm.predicate.equals(toResolve.predicate)) {
                        //if (!factTerm.predicate.equals(toResolve.predicate)) {
                    //if(!predicateEqualsWithoutNegation(factTerm,toResolve)){
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            list.add(s);
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                }
                for (Predicate factTerm : resolved.predicateList) {
                    if((factTerm.predicate.equals(toResolve.predicate)) && (!factTerm.toResolve)) {
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            list.add(s);
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                    else if (!factTerm.predicate.equals(toResolve.predicate)) {
                        //if (!factTerm.predicate.equals(toResolve.predicate)) {
                   // if(!predicateEqualsWithoutNegation(factTerm,toResolve)){
                        Predicate newTerm;
                        ArrayList<String> list = new ArrayList<String>();
                        for (String s : factTerm.args) {
                            list.add(s);
                        }
                        newTerm = new Predicate(list, factTerm.negation, factTerm.predicate);
                        listTerms.add(newTerm);
                    }
                }

                if(listTerms.size()==0)
                    return null;

                resolvedFact = new Rule(listTerms);
                resolvedFact = factorize(resolvedFact);

                //if (resolvedSentence.terms.isEmpty()) {
                //    return result;
                //}
            }
            return resolvedFact;
        }

        // to remove redundant terms form resolved sentences
        public Rule factorize(Rule s) {
            boolean removed[] = new boolean[s.predicateList.size()];
            ArrayList<Predicate> termsList = s.predicateList;
            Rule result = new Rule();
            ArrayList<Predicate> resultTerms = new ArrayList<Predicate>();
            resultTerms.addAll(s.predicateList); // check if addAll works
            for (int i = 0; i < termsList.size(); i++) {
                for (int j = 0; j < termsList.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    if (termsList.get(i).predicate.equals(termsList.get(j).predicate) && termsList.get(i).negation == termsList.get(j).negation) {
                        if (allVariables(termsList.get(i)) && resultTerms.size()>2 && !removed[i]) {
                            resultTerms.remove(termsList.get(i));
                            removed[i] = true;
                            removed[j] = true;
                        } else if (allVariables(termsList.get(j)) && resultTerms.size()>2 && !removed[j]) {
                            resultTerms.remove(termsList.get(i));
                            removed[j] = true;
                            removed[i] = true;
                        }
                    }
                }
            }
            result.predicateList = resultTerms;
            return result;
        }

        //check if all arguments are variables
        public boolean allVariables(Predicate t) {
            for (String s : t.args) {
                if (!isVariable(s)) {
                    return false;
                }
            }
            return true;
        }


        // unify will be called on those sentences which have a matching predicate with query
        public HashMap<String,String> unifyPredicate(HashMap<String, String> theta, Predicate x, Predicate y){
//            if(theta == null){
//                return null;
//            }
            if(x.predicate.equals(y.predicate) ){
                for(int i=0;i<x.args.size();i++){
                    //System.out.println(x.args.get(i)+"::"+y.args.get(i));
                    theta = unifyVariable(theta,x.args.get(i),y.args.get(i));
                    if(theta == null)
                        return null;
                }
            }
            return theta;
        }


        // unify with variables
        public HashMap<String,String> unifyVariable(HashMap<String,String> theta, String arg1, String arg2){

            if(arg1.equals(arg2)) {
                return theta;
            }

            else if(isVariable(arg1)){
                return unifyVariableConstants(theta,arg1,arg2);
            }
            else if(isVariable(arg2)){
                return unifyVariableConstants(theta,arg2,arg1);
            }
            //ToDo: if both are variables??
            else
                return null;

        }


        // unify with constants
        public HashMap<String,String> unifyVariableConstants(HashMap<String,String> theta,String x, String y){
            if(theta.containsKey(x)){
                //System.out.println("Has Key "+x);
                return unifyVariable(theta,theta.get(x),y);
            }
            else if(theta.containsKey(y)){
                //System.out.println("Has Key y "+y);
                return unifyVariable(theta,x,theta.get(y));
            }
            else{
                //System.out.println("Inserted Key in HashMap "+x+"::"+y);
                theta.put(x,y);
                return theta;
            }
        }

        public boolean isConstant(String arg){
            if(Character.isUpperCase(arg.charAt(0)))
            {
                return true;
            }
            else return false;
        }

        public boolean isVariable(String arg){
            if(Character.isLowerCase(arg.charAt(0)))
            {
                return true;
            }
            else return false;
        }



        //unify will return only value to be substituted
        //ToDo: substitute unified value in entire rule:
        public void substituteUnifiedValue(Rule rl, HashMap<String,String> mapping){

        }

        //ToDo: Rules equals

        //ToDo: Predicate Equals
        public boolean predicateEquals(Predicate a, Predicate b){
            if((a.negation == b.negation)&&(a.predicate == b.predicate)){
                int cnt = 0;
                for(int i=0;i<a.args.size();i++){
                    if(a.args.get(i).equals(b.args.get(i))){
                        cnt++;
                    }
                }
                if(cnt == a.args.size()){
                    return true;
                }
            }
            return false;
        }

        public boolean predicateEqualsWithoutNegation(Predicate a, Predicate b){
            if(a.predicate == b.predicate){
                int cnt = 0;
                for(int i=0;i<a.args.size();i++){
                    if(a.args.get(i).equals(b.args.get(i))){
                        cnt++;
                    }
                }
                if(cnt == a.args.size()){
                    return true;
                }
            }
            return false;
        }


}
