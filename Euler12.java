public class Euler12 {
    public static void main(String[] args) {
        int minDivs = 500;
        System.out.println("First triangular number with at least " + minDivs + " divisors is " + firstTriNum(minDivs));
    }

    //returns the first triangular number with at least the given number of divisors
    public static int firstTriNum(int minDivs) {
        PrimeList primes = new PrimeList();
        int n = 1;
        int curDivs = 0;

        //only a triangular number > minDiv can have at least minDivs divisors
        while (triNum(n) <= minDivs) n++;

        while (curDivs <= minDivs) {
            //since n and n+1 share no prime factors, numDiv(n*n+1) = numDiv(n) * numDiv(n+1)
            //either n or n+1 will have 2 as a prime factor
            //so, the prime factorization of n(n+1) = (2^e)(p2^e2)(p3^e3)... (where e >= 1)
            //which means numDiv(n*n+1) = (e+1)(e2+1)(e3+1)...
            //if e > 1, then 2 is a prime factor of triNum(n), so numDiv(triNum(n)) = (e)(e2+1)(e3+1)...
            //if e = 1, then 2 is not a prime factor of triNum(n), so numDiv(triNum(n)) = (e2+1)(e3+1)... = (numDiv(n)*numDiv(n+1))/2

            int divisorProduct = primes.numDivisors(n) * primes.numDivisors(n+1);

            //check if product has at least minDivs, since numDivisors(n*n+1) > numDivisors(triNum(n))
            if (divisorProduct > minDivs) {
                int checkExp = n*(n+1); //used to find numDivisors(triNum(n))
                assert(checkExp % 2 == 0); //n(n+1) is even
                checkExp /= 2;
                int expCount = 1; //count of 2s in prime factorization of n(n+1)

                if (checkExp % 2 != 0){ //if 2 only divides n(n+1) once,
                    //numDivisors(triNum(n)) = (numDiv(n) * numDiv(n+1))/2
                    curDivs = divisorProduct/2;
                }
                else {
                    while (checkExp % 2 == 0) {
                        checkExp /= 2;
                        expCount++;
                    }
                    //numDiv(n(n+1)) = (e+1)(e2+1)(e3+1)... (where e = exponent of 2)
                    //adjust to (e)(e2+1)(e3+1) = numDivisors(triNum(n))
                    divisorProduct /= expCount+1;
                    divisorProduct *= expCount;
                    curDivs = divisorProduct;
                }

                if (curDivs < minDivs) n++;
            }
            else n++; //n(n+1) didn't have enough divisors
        }

        return triNum(n);
    }//firstTriNum()

    public static int triNum(int n) {
        return (n * (n+1)) / 2;
    }
}//Euler12

class Node {
    private int item;
    private Node next;

    public Node(int item) {
        this.item = item;
        next = null;
    }

    public int getItem() {
        return item;
    }

    public void setNext(Node node) {
        next = node;
    }

    public Node getNext() {
        return next;
    }
}//Node

class PrimeList {
    private Node head;
    private Node tail;

    public PrimeList() {
        head = null;
        tail = null;
        init();
    }

    private void add(int n) {
        Node node = new Node(n);

        if (head == null) head = node;
        else tail.setNext(node);

        tail = node;
    }

    public int getLast() {
        assert (tail != null); //never null since list is initialized
        return tail.getItem();
    }

    private void init() {
        add(2);
        add(3);
    }

    //adds the next prime to the list
    public void findNextPrime() {
        int cand = getLast();
        int prime = 0;
        boolean done = false;
        Node iter;

        while (!done) {
            //next candidate, restart at beginning of primes
            iter = head;
            cand += 2;

            //check if candidate divides any of the other primes in the list
            while (iter != null && prime <= Math.sqrt(cand)) {
                prime = iter.getItem();
                if (cand % prime == 0) break;
                iter = iter.getNext();
            }

            //if the candidate is not divisible by any prime greater than its sqrt, the candidate is prime
            if (prime > Math.sqrt(cand)) done = true;
        }

        add(cand);
    }//findNextPrime()

    //determines the number of divisors for a given number
    public int numDivisors(int n) {
        //prime factorization of n = (p1^e1)(p2^e2)(p3^e3)... where pn is a prime and en is its exponent
        //number of divisors of n = (e1+1)(e2+1)(e3+1)...
        //algorithm:
        //set divisors to 1
        //set count to 0
        //for each prime p,
        //divide n by p as many times as possible as long as there's no remainder, incrementing count each time
        //multiply divisors by count+1

        //get the necessary primes
        while (getLast() < n) findNextPrime();

        int numDivisors = 1;
        int prime = 0; //current prime dividing n
        int count; //number of times current prime divides n
        Node iter = head; //holds node with current prime
        int cur = n; //used to divide n by primes repeatedly

        while (iter != null && prime <= n) {
            prime = iter.getItem(); //get the next prime
            count = 0; //reset count

            while (cur % prime == 0) { //divide by prime as many times as possible
                cur /= prime;
                count++;
            }

            numDivisors *= count + 1; //update number of divisors
            iter = iter.getNext(); //move to next node
        }

        return numDivisors;
    }//numDivisors()
}//PrimeList
