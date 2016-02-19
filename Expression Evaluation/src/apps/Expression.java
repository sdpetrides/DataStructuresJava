package apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
	/**
	 * Positions of opening brackets
	 */
	ArrayList<Integer> openingBracketIndex; 
    
	/**
	 * Positions of closing brackets
	 */
	ArrayList<Integer> closingBracketIndex; 

    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
        scalars = null;
        arrays = null;
        openingBracketIndex = null;
        closingBracketIndex = null;
    }

    /**
     * Matches parentheses and square brackets. Populates the openingBracketIndex and
     * closingBracketIndex array lists in such a way that closingBracketIndex[i] is
     * the position of the bracket in the expression that closes an opening bracket
     * at position openingBracketIndex[i]. For example, if the expression is:
     * <pre>
     *    (a+(b-c))*(d+A[4])
     * </pre>
     * then the method would return true, and the array lists would be set to:
     * <pre>
     *    openingBracketIndex: [0 3 10 14]
     *    closingBracketIndex: [8 7 17 16]
     * </pe>
     * 
     * See the FAQ in project description for more details.
     * 
     * @return True if brackets are matched correctly, false if not
     */
    public boolean isLegallyMatched() {
    	Stack<Bracket> sk = new Stack<Bracket>();
    	
    	this.openingBracketIndex = new ArrayList<Integer>();
    	this.closingBracketIndex = new ArrayList<Integer>();
    	for(int i = 0; i <=this.expr.length()-1; i++){
    		if (this.expr.charAt(i) == '(' || this.expr.charAt(i) == '['){
    			if (this.expr.charAt(i) == '('){
    				sk.push(new Bracket('(',i));
    			} else {
    				sk.push(new Bracket('[',i));
    			}
    		} else if (this.expr.charAt(i) == ')' || this.expr.charAt(i) == ']'){
    			if (sk.size()==0){
    				return false;
    			}
    			Bracket br = sk.pop();
    			if (this.expr.charAt(i) == ')' && br.ch == '['){
    				return false;
    			} else if (this.expr.charAt(i) == ']' && br.ch == '('){
    				return false;
    			} else {
    				this.openingBracketIndex.add(br.pos);
    				this.closingBracketIndex.add(i);
    			}
    		}
    	}
    	if (sk.size() == 0 && this.openingBracketIndex.size() == this.closingBracketIndex.size()){return true;}
    	return false;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, the constructors for ScalarSymbol and ArraySymbol
     * will initialize values to zero and null, respectively.
     * The actual values will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    	
    	this.scalars = new ArrayList<ScalarSymbol>();
    	this.arrays = new ArrayList<ArraySymbol>();
    	int firstChar = 0;
    	int lastChar = 0;
    	boolean marked = false;
    	for (int i = 0; i <= this.expr.length()-1; i++){
    		if (this.expr.charAt(i) != '*' && this.expr.charAt(i) != '+' && this.expr.charAt(i) != '-' && this.expr.charAt(i) != '/' &&
    				this.expr.charAt(i) != ' ' && this.expr.charAt(i) != '(' && this.expr.charAt(i) != '[' && this.expr.charAt(i) != ']' &&
    				this.expr.charAt(i) != ')' && this.expr.charAt(i) != '0' && this.expr.charAt(i) != '1' && this.expr.charAt(i) != '2' 
    				&& this.expr.charAt(i) != '3' && this.expr.charAt(i) != '4' && this.expr.charAt(i) != '5' && this.expr.charAt(i) != '6' 
    				&& this.expr.charAt(i) != '7' && this.expr.charAt(i) != '8' && this.expr.charAt(i) != '9'){
    			if (!marked){
    				firstChar = i;
    				marked = true;
    			}
    			if(i != this.expr.length()-1){
    				continue;
    			}
    		}
    		lastChar = i-1;
    		if (i == this.expr.length()-1 && this.expr.charAt(i) != '*' && this.expr.charAt(i) != '+' && this.expr.charAt(i) != '-' && this.expr.charAt(i) != '/' &&
    				this.expr.charAt(i) != ' ' && this.expr.charAt(i) != '(' && this.expr.charAt(i) != '[' && this.expr.charAt(i) != ']' &&
    				this.expr.charAt(i) != ')' && this.expr.charAt(i) != '0' && this.expr.charAt(i) != '1' && this.expr.charAt(i) != '2' 
    				&& this.expr.charAt(i) != '3' && this.expr.charAt(i) != '4' && this.expr.charAt(i) != '5' && this.expr.charAt(i) != '6' 
    				&& this.expr.charAt(i) != '7' && this.expr.charAt(i) != '8' && this.expr.charAt(i) != '9'){lastChar = this.expr.length()-1;}
    		if (this.expr.charAt(i) == '[' && marked){
    			if (newVarAR(this.expr.substring(firstChar, lastChar+1))){
    				ArraySymbol ar = new ArraySymbol(this.expr.substring(firstChar, lastChar+1));
    				this.arrays.add(ar);
    				marked = false;
    			}
    		} else if (marked){
    			if (newVarSC(this.expr.substring(firstChar, lastChar+1))){
    				if (ArrayNotInScalar(this.expr.substring(firstChar, lastChar+1))){
    					ScalarSymbol sc = new ScalarSymbol(this.expr.substring(firstChar, lastChar+1));
    					this.scalars.add(sc);
    					marked = false;
    				}
    			}
    			marked = false;
    		}
    	}
    }
    
    /** Helper Method: Checks if array bracket is in scalar
     * 
     * @author Steve
     * @param prospective string t
     */
    private boolean ArrayNotInScalar(String t){
    	
    	for (int i = 0; i < t.length(); i++){
    		if (t.charAt(i) == '['){return false;}
    	}
    
    	return true;
    }
    
    /** Helper Method: Checks if varible was aready created
     * 
     * @author Steve
     * @param prospective string t
     */
    private boolean newVarSC(String t){
    	for (int i = 0; i < this.scalars.size(); i++){
    		if (this.scalars.get(i).name.equals(t)){return false;}
    	}
    	return true;
    }
    
    
    /** Helper Method: Checks if varible was aready created
     * 
     * @author Steve
     * @param prospective string t
     */
    private boolean newVarAR(String t){ 
    	for (int i = 0; i < this.arrays.size(); i++){
    		if (this.arrays.get(i).name.equals(t)){return false;}
    	}
    	return true;
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions. (Note: you can use one or more private helper methods
     * to implement the recursion.)
     * 
     * @return Result of evaluation
     */
    public float evaluate() {

    	this.isLegallyMatched();
    	
    	// put in scalar values on first run through 
    	int start = 0;
    	boolean mark = false;
    	if (this.scalars != null){
    		for (int i = 0; i <= this.expr.length()-1; i++){
    			for (int j = 0; j <= this.scalars.size()-1; j++){
    				if (i <= this.expr.length()-1 && this.scalars.get(j).name.charAt(0)==this.expr.charAt(i)){
    					start = i; int k = 0; mark = true;
    					for (; k<=this.scalars.get(j).name.length()-1;k++,i++){
    						if (this.scalars.get(j).name.charAt(k)!=this.expr.charAt(i)){
    							mark = false;
    							break;
    						}
    					}
    					if (mark){
    						this.expr = this.expr.substring(0,start) + this.scalars.get(j).value + this.expr.substring(start+k);
    						int l = i - start;
    						i = i - l + String.valueOf(this.scalars.get(j).value).length();
    					} else {
    						i = i - k;
    					}
    				}
    			}
    		}
    	}
    	// recurse through arrays first
    	while (this.arrays != null){
    		int maxValue = 0;
    		int maxIndexBracket = 0;
    		for (int i = 0; i <= this.openingBracketIndex.size()-1; i++){
    			if (this.expr.charAt(this.openingBracketIndex.get(i)) == '[' && this.openingBracketIndex.get(i) > maxValue){
    				maxValue = this.openingBracketIndex.get(i);
    				maxIndexBracket = i;		
    			}
    		}
    		if (maxValue == 0){break;}
    		Expression ex = new Expression(this.expr.substring(this.openingBracketIndex.get(maxIndexBracket)+1,this.closingBracketIndex.get(maxIndexBracket)));
    		int j;
    		for (j = this.openingBracketIndex.get(maxIndexBracket)-1; j >= 0; j--){
    			if (this.expr.charAt(j) == '*' || this.expr.charAt(j) == '+' || this.expr.charAt(j) == '-' || this.expr.charAt(j) == '/' ||
        				this.expr.charAt(j) == ' ' || this.expr.charAt(j) == '(' || this.expr.charAt(j) == '[' || this.expr.charAt(j) == ']' ||
        				this.expr.charAt(j) == ')' || this.expr.charAt(j) == '0' || this.expr.charAt(j) == '1' || this.expr.charAt(j) == '2' 
        				&& this.expr.charAt(j) == '3' || this.expr.charAt(j) == '4' || this.expr.charAt(j) == '5' || this.expr.charAt(j) == '6' 
        				&& this.expr.charAt(j) == '7' || this.expr.charAt(j) == '8' || this.expr.charAt(j) == '9'){
    				break;
    			}
    		}
    		String var = this.expr.substring(j+1,this.openingBracketIndex.get(maxIndexBracket));
    		int k = 0;
    		for (; k <= this.arrays.size()-1; k++){
    			if (this.arrays.get(k).name.equals(var)){
    				break;
    			}
    		}
    		int originalLength = this.expr.length();
    		this.expr.substring(this.closingBracketIndex.get(maxIndexBracket)+1);
			this.expr = this.expr.substring(0, this.openingBracketIndex.get(maxIndexBracket)-var.length()) + 
					this.arrays.get(k).values[(int)ex.evaluate()] + 
					this.expr.substring(this.closingBracketIndex.get(maxIndexBracket)+1);
			int dif = originalLength-this.expr.length();
			
    		int openingMaxIndexBracket = this.openingBracketIndex.get(maxIndexBracket);
    		int closingMaxIndexBracket = this.closingBracketIndex.get(maxIndexBracket);
			this.openingBracketIndex.remove(maxIndexBracket);
    		this.closingBracketIndex.remove(maxIndexBracket);

    		
    		// update other bracket indexes
    		for (int p = 0; p <= this.closingBracketIndex.size()-1; p++){
    			// if inside array
    			if (openingMaxIndexBracket <= this.openingBracketIndex.get(p) && closingMaxIndexBracket <= this.closingBracketIndex.get(p)){
    				this.openingBracketIndex.remove(p);
    				this.closingBracketIndex.remove(p);
    			}
    			if (closingMaxIndexBracket <= this.closingBracketIndex.get(p)){
    				int updatedIndex = this.closingBracketIndex.get(p)-dif;
    				this.closingBracketIndex.remove(p);
    				this.closingBracketIndex.add(p, updatedIndex);
    			}
    			if (openingMaxIndexBracket <= this.openingBracketIndex.get(p)){
    				int updatedIndex = this.openingBracketIndex.get(p)-dif;
       			 	this.openingBracketIndex.remove(p);
       			 	this.openingBracketIndex.add(p, updatedIndex);
    			}
    		}
    	}
    	
    	
    	// if parenthesis in first and last position and last position match
    	for (int i = 0; i <= this.openingBracketIndex.size()-1; i++){
			if (this.openingBracketIndex.get(i) == 0 && this.closingBracketIndex.get(i) == this.expr.length()-1){
				Expression ex = new Expression(this.expr.substring(1,this.closingBracketIndex.get(i)));
				return ex.evaluate();
			}
		}
    	for (int i = 0; i <= this.expr.length()-1; i++){
    		if (this.expr.charAt(i) == ' '){
    			this.expr = this.expr.substring(0,i) + this.expr.substring(i+1);
    		}
    	}
    	boolean outsideParen = true;
    	// split at addition, subtraction
    	for (int i = this.expr.length()-1; i >= 0; i--){
    		outsideParen = true;
        	if (this.expr.charAt(i) == '+'){
        		for (int j=0; j <= this.openingBracketIndex.size()-1; j++){
        			if (i > this.openingBracketIndex.get(j) && i < this.closingBracketIndex.get(j)){
        				outsideParen = false;
        			}
        		}
        		if (outsideParen){
        		Expression beg = new Expression(this.expr.substring(0, i));
        		Expression end = new Expression(this.expr.substring(i+1));
        		return beg.evaluate() + end.evaluate();
        		}
        	} else if (this.expr.charAt(i) == '-'){
        		for (int j=0; j <= this.openingBracketIndex.size()-1; j++){
        			if (i > this.openingBracketIndex.get(j) && i < this.closingBracketIndex.get(j)){
        				outsideParen = false;
        			}
        		}
        		if (outsideParen){
        		Expression beg = new Expression(this.expr.substring(0, i));
        		Expression end = new Expression(this.expr.substring(i+1));
        		return beg.evaluate() - end.evaluate();
        		}
        	} 
    	}
    	// split at addition, subtraction
        for (int i = 0; i <= this.expr.length()-1; i++){	
        	if (this.expr.charAt(i) == '*'){
        		Expression beg = new Expression(this.expr.substring(0, i));
        		Expression end = new Expression(this.expr.substring(i+1));
       			return beg.evaluate() * end.evaluate();
       		} else if (this.expr.charAt(i) == '/'){
       			Expression beg = new Expression(this.expr.substring(0, i));
        		Expression end = new Expression(this.expr.substring(i+1));
        		return beg.evaluate() / end.evaluate();
       		}		
       	}
        
    	// down to a single term (base-case)
    	if (this.expr.charAt(0)=='0' || this.expr.charAt(0)=='1' || this.expr.charAt(0)=='2' || this.expr.charAt(0)=='3' ||
    		this.expr.charAt(0)=='4' || this.expr.charAt(0)=='5' || this.expr.charAt(0)=='6' || this.expr.charAt(0)=='7' ||
    		this.expr.charAt(0)=='8' || this.expr.charAt(0)=='9'){
    			int f = Integer.parseInt(this.expr);
    		    return f;
    	}
    	
    	return 0; // make compiler happy
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    	for (ArraySymbol as: arrays) {
    		System.out.println(as);
    	}
    }

}
