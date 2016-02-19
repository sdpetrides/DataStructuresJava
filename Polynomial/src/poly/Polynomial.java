package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		
		
		if (this.poly == null){
			return p;
		}
		
		Polynomial newPoly = new Polynomial();
		newPoly.poly = null;
		boolean move = false;
		boolean end = true;
		
		for (; this.poly != null; this.poly = this.poly.next){
			if (move && p.poly != null){
				p.poly = p.poly.next;
			}
			end = true;
			for (; p.poly != null;){
				if (this.poly.term.degree == p.poly.term.degree){
					addToBack(newPoly, this.poly.term.coeff+p.poly.term.coeff, this.poly.term.degree);
					move = true;
					end = false;
					break;
				} else if ((this.poly.term.degree < p.poly.term.degree)){
					addToBack(newPoly, this.poly.term.coeff, this.poly.term.degree);
					move = false;
					end = false;
					break;
				} else if ((this.poly.term.degree > p.poly.term.degree)){
					addToBack(newPoly, p.poly.term.coeff, p.poly.term.degree);
					move = true;
					end = false;
					p.poly = p.poly.next;
					continue;
				}
			}
			if (end || (end == false && p.poly == null)){
				addToBack(newPoly, this.poly.term.coeff, this.poly.term.degree);
			}
		}
		
		if (!end && p.poly != null){
			p.poly = p.poly.next;
			while(p.poly != null){
				addToBack(newPoly, p.poly.term.coeff, p.poly.term.degree);
				p.poly = p.poly.next;
			}
		}
		
		newPoly = deleteZeros(newPoly);
		
		return newPoly;
	}
	
	/** Helper Method to add at end of polynomial
	 * 
	 * @param p Polynomial, coeff, degree;
	 * @author Stephen Petrides
	 * @return void
	 */
	private static void addToBack(Polynomial p, float coeff, int degree){
		if (p.poly == null){
			p.poly = new Node (coeff, degree, null);
			return;
		}
		
		for (Node ptr = p.poly;;ptr = ptr.next){
			if (ptr.next == null){
				ptr.next = new Node (coeff, degree, null);
				return;
			}
		}
	}
	
	/** Helper Method to delete all nodes with zero coefficients
	 * 
	 * @param p Polynomial
	 * @author Stephen Petrides
	 * @return Polynomial
	 */
	private static Polynomial deleteZeros(Polynomial p){
		if (p == null || p.poly == null){
			return p;
		}
		
		Polynomial newPoly = new Polynomial();
		newPoly.poly = null;
		
		for (; p.poly != null; p.poly = p.poly.next){
			if (p.poly.term.coeff != 0){
				addToBack(newPoly, p.poly.term.coeff, p.poly.term.degree);
			}
		}
		
		return newPoly;
		
	}
	
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		
		if (this.poly == null){
			return p;
		}
		
		Polynomial newPoly = new Polynomial();
		newPoly.poly = null;
		
		
		// multiply it out
		for(; this.poly != null; this.poly = this.poly.next){
			for(Node ptr = p.poly; ptr != null; ptr = ptr.next){
				addToBack(newPoly, this.poly.term.coeff*ptr.term.coeff, this.poly.term.degree+ptr.term.degree);
			}
		}
		
		//sort and add nodes
		
		Polynomial multPoly = new Polynomial();
		multPoly.poly = null;
		
		multPoly = sort(newPoly);
		multPoly = deleteZeros(multPoly);
		
		return multPoly;
	}
	
	/** Helper Method to sort polynomial
	 * 
	 * @param p Polynomial
	 * @author Stephen Petrides
	 * @return Polynomial
	 */
	private static Polynomial sort(Polynomial p){
		if (p == null || p.poly == null){
			return p;
		}
		
		Polynomial newPoly = new Polynomial();
		newPoly.poly = null;
		int i = 0;
		float j = 0;
		
		// find highest degree
		for (Node front = p.poly; front != null; front = front.next){
			i = front.term.degree;
		}
			
			
		// aggregate and create new 
		for(int k = 0; k <= i; k++){
			j = 0;
			for(Node ptr = p.poly; ptr != null; ptr = ptr.next){
				if (ptr.term.degree == k){
					j = j + ptr.term.coeff;
				}
				
			}
			
			addToBack(newPoly, j, k);;
		
		}
		
		return newPoly;
		
	}
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		if (this.poly == null){
			return 0;
		}
		float sum = 0;
		
		for (Polynomial ptr = this; ptr.poly != null; ptr.poly = ptr.poly.next){
			if (ptr.poly.term.degree == 0){
				sum = sum + ptr.poly.term.coeff;
				//System.out.println("Sum: " + sum);
				continue;
			}
			float xSum = 1;
			for (int i = 1; i<=ptr.poly.term.degree; i++){
				xSum = xSum*x;
			}
			sum = sum + ptr.poly.term.coeff*xSum;
			//System.out.println("Sum: " + sum);
		}
		
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
