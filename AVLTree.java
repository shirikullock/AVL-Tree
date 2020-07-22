//Shiri Kullock, id 312530686, user name shirikullock
//Yaara Federman, id 205946312, user name yaaraf

/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

	private final IAVLNode EXTERNAL = new AVLNode(-1, null); 
	private IAVLNode root;
	private IAVLNode min;
	private IAVLNode max;
	private int size;
	
	/**
	 * constructor
	 * 
	 */
	public AVLTree() {
		this.root = null;
		this.max = null;
		this.min = null;
		this.size = 0;
	}
	
	/**
	 * private void setTreeWithRoot(IAVLNode newRoot)
	 * updates the tree's root and other fields accordingly
	 * 
	 */
	private void setTreeWithRoot(IAVLNode newRoot) {
		this.root = newRoot;
		this.root.setParent(null);
		this.max = this.findMax();
		this.min = this.findMin();
		this.size = this.root.getSize();
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return (this.root == null);
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k) {
		if (this.empty()) {
			return null;
		}
		IAVLNode curr = this.root;
		while (curr.getHeight() != -1) {
			if (curr.getKey() == k) {
				return curr.getValue();
			}
			if (curr.getKey() > k) {
				curr = curr.getLeft();
			} else {
				curr = curr.getRight();
			}
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * returns -1 if an item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {
		if (empty()) {
			this.root = new AVLNode(k,i);
			this.size ++;
			this.min = this.root;
			this.max = this.root;
			return 0;	
		}
		IAVLNode where = whereToInsert(k);
		if (where.getHeight() == -1) { //a node with key k already exists in the tree 
			return -1;
		}
		IAVLNode toInsert = new AVLNode(k,i);
		fixMinMaxInsert(toInsert);
		toInsert.setParent(where);
		if (where.getKey() < k) {
			where.setRight(toInsert);
		} else {
			where.setLeft(toInsert);
		}
		fixSizeTree(where, 1);
		this.size ++;
		return rebalanceInsert(where);
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		if (this.root == null) {
			return -1;
		}
		IAVLNode where = searchNode(k);
		if (where.getHeight() == -1) { //no such node in the tree
			return -1;
		}
		if (where == this.root && this.size == 1) {
			this.root = null;
			this.size = 0;
			this.min = null;
			this.max = null;
			return 0;
		}
		
		fixMinMaxDelete(k);
		IAVLNode curr = deleteForReal(where);
		if (curr != null) {
			fixSizeTree(curr, -1);
		}
		this.size --;
		return rebalanceDelete(curr);
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */
	public String min() {
		if (min == null) {
			return null;
		}
		return min.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max() {
		if (max == null) {
			return null;
		}
		return max.getValue();
	}

	/**
	 * public int[] keysToArray()
	 * 
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	public int[] keysToArray() {
		int[] arr = new int[this.size]; // to be replaced by student code
		keysToArrayRec(arr, this.root, 0);
		return arr;
	}

	/**
	 * private int keysToArrayRec(int[] arr, IAVLNode curr, int loc)
	 * recursively insetrs the tree's nodes' keys into an array
	 * returns the current location in arr
	 * 
	 */
	private int keysToArrayRec(int[] arr, IAVLNode curr, int loc) {
		if (curr.getKey() == -1) {
			return loc;
		}
		loc = keysToArrayRec(arr, curr.getLeft(), loc);
		arr[loc] = curr.getKey();
		loc++;
		loc = keysToArrayRec(arr, curr.getRight(), loc);
		return loc;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		String[] arr = new String[this.size]; 
		infoToArrayRec(arr, this.root, 0);
		return arr;
	}
	
	/**
	 * 	private int infoToArrayRec(String[] arr, IAVLNode curr, int loc) 
	 * recursively insetrs the tree's nodes' values into an array
	 * returns the current location in arr
	 */
	private int infoToArrayRec(String[] arr, IAVLNode curr, int loc) {
		if (curr.getKey() == -1) {
			return loc;
		}
		loc = infoToArrayRec(arr, curr.getLeft(), loc);
		arr[loc] = curr.getValue();
		loc++;
		loc = infoToArrayRec(arr, curr.getRight(), loc);
		return loc;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public int size() {
		return this.size; 
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/** 
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. 
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * precondition: search(x) != null
	 * postcondition: none
	 */   
	public AVLTree[] split(int x) {
		AVLTree bigger = new AVLTree();
		AVLTree smaller = new AVLTree();
		IAVLNode curr = searchNode(x);
		if (curr.getLeft().getHeight() != -1) {
			smaller.setTreeWithRoot(curr.getLeft());
		}
		if (curr.getRight().getHeight() != -1){
			bigger.setTreeWithRoot(curr.getRight());
		}
		IAVLNode parent = curr.getParent();
		AVLTree toJoin = new AVLTree();
		while (parent != null) {
			if (parent.getRight() == curr) {
				curr = parent;
				parent = parent.getParent();
				if (curr.getLeft().getHeight() != -1) {
					toJoin.setTreeWithRoot(curr.getLeft());
				} else {
					toJoin = new AVLTree();
				}
				curr.setParent(null);
				curr.setLeft(EXTERNAL);
				curr.setRight(EXTERNAL);
				curr.setHeight(0);
				curr.setSize(1);
				smaller.join(curr, toJoin);
			} else {
				curr = parent;
				parent = parent.getParent();
				if (curr.getRight().getHeight() != -1) {
					toJoin.setTreeWithRoot(curr.getRight());
				} else {
					toJoin = new AVLTree();
				}
				curr.setParent(null);
				curr.setLeft(EXTERNAL);
				curr.setRight(EXTERNAL);
				curr.setHeight(0);
				curr.setSize(1);
				bigger.join(curr, toJoin);
			}
		}
		return new AVLTree[] {smaller,bigger};
	}

	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. 	
	 * Returns the complexity of the operation (rank difference between the tree and t)
	 * precondition: keys(x,t) < keys() or keys(x,t) > keys()
	 * postcondition: none
	 */   
	public int join(IAVLNode x, AVLTree t) {
		if (t.empty() && this.empty()) {
			this.root = x;
			this.min = x;
			this.max = x;
			this.size ++;
			x.setParent(null);
			x.setLeft(EXTERNAL);
			x.setRight(EXTERNAL);
			x.setSize(1);
			x.setHeight(0);
		}
		if (t.empty()) {
			int height = this.root.getHeight() + 1;
			insert(x.getKey(), x.getValue());
			return height;
		}
		if (this.empty()) { 
			this.root = t.getRoot();
			this.min = t.min;
			this.max = t.max;
			this.size = t.size;
			int height = t.root.getHeight() + 1;
			insert(x.getKey(), x.getValue());
			return height;
		} 
		AVLTree bigger = t;  // if the keys in "t" are bigger then "x"
		AVLTree smaller = this; // if the keys in "t" are bigger then "x"
		if (this.root.getKey() > x.getKey()) { //if the keys in "this" are bigger than "x"
			bigger = this;
			smaller = t;
		}
		int diff;
		boolean biggerIsHeigher = true;
		IAVLNode where;
		if (bigger.root.getHeight() > smaller.root.getHeight()) {
			diff = bigger.root.getHeight() - smaller.root.getHeight();
			if (diff == 0) { //the trees have the same height
				joinEquals(smaller, x, bigger);
				return diff + 1;
			}
			where = searchOnLeft(bigger, smaller.root.getHeight()); //searches the joining node
			if (where == bigger.root) {
				joinEquals(smaller, x, bigger);
				return diff + 1;
			}
			joinForReal(x, where, smaller.root);
		} else {
			diff = smaller.root.getHeight() - bigger.root.getHeight();
			if (diff == 0) { //the trees have the same height
				joinEquals(smaller, x, bigger);
				return diff + 1;
			}
			biggerIsHeigher = false;
			where = searchOnRight(smaller, bigger.root.getHeight());  //searches the joining node
			if (where == smaller.root) {
				joinEquals(smaller, x, bigger);
				return diff + 1;
			}
			joinForReal(x, where, bigger.root);
		}
		
		// updates "this" fields
		int sizeToAdd = 0; 
		if (biggerIsHeigher) {
			this.root = bigger.root;
			sizeToAdd = smaller.size + 1;
		} else {
			this.root = smaller.root;
			sizeToAdd = bigger.size + 1;
		}
		fixSizeTree(x.getParent(), sizeToAdd);
		this.min = smaller.min;
		this.max = bigger.max;
		rebalanceInsert(x.getParent());
		return diff + 1;
	}

	/**
	 * 	private void rotateLeft(IAVLNode x, IAVLNode y) 
	 * Rotates the tree left on x-y edge
	 * x is the higher node
	 */
	private void rotateLeft(IAVLNode x, IAVLNode y) {
		IAVLNode tmp = y.getLeft();
		y.setLeft(x);
		x.setRight(tmp);
		tmp.setParent(x);
		tmp = x.getParent();
		x.setParent(y);
		y.setParent(tmp);
		if (tmp != null) {
			if (tmp.getRight() == x) {
				tmp.setRight(y);
			} else {
				tmp.setLeft(y);
			}
		} else {
			this.root = y;
		}
		fixSizeNode(x);
		fixSizeNode(y);
	}

	/**
	 * 	private void rotateRight(IAVLNode y, IAVLNode y) 
	 * Rotates the tree right on y-x edge
	 * y is the higher node
	 */
	private void rotateRight(IAVLNode y, IAVLNode x) {
		IAVLNode tmp = x.getRight();
		y.setLeft(tmp);
		x.setRight(y);
		tmp.setParent(y);
		tmp = y.getParent();
		y.setParent(x);
		x.setParent(tmp);
		if (tmp != null) {
			if (tmp.getRight() == y) {
				tmp.setRight(x);
			} else {
				tmp.setLeft(x);
			}
		} else {
			this.root = x;
		}
		fixSizeNode(y);
		fixSizeNode(x);
	}

	/**
	 * 	private static void fixHeight(IAVLNode curr) 
	 * corrects the height of curr according to it's children 
	 * this function does promote or demote
	 */
	private static void fixHeight(IAVLNode curr) {
		int max = Math.max(curr.getLeft().getHeight(), curr.getRight().getHeight());
		curr.setHeight(max+1);
	}
	
	/**
	 * 	private static void fixSizeNode(IAVLNode curr) 
	 * corrects the size of curr according to it's children
	 */
	private static void fixSizeNode(IAVLNode curr) {
		int size = curr.getLeft().getSize() + curr.getRight().getSize();
		curr.setSize(size+1);
	}
	
	/** 
	 * 	private IAVLNode whereToInsert(int k) 
	 * finds where to insert a new Node with the key k
	 * returns an external leaf if the key already exists 
	 */
	private IAVLNode whereToInsert(int k) {
		IAVLNode curr = this.root;
		IAVLNode prev = this.root;
		while (curr.getKey() != -1) {
			int currKey = curr.getKey();
			if (currKey == k) {
				return EXTERNAL;
			}
			if (currKey < k) {
				prev = curr;
				curr = curr.getRight();
			} else {
				prev = curr;
				curr = curr.getLeft();
			}
		}
		return prev;
	}
	
	/**
	 * 	private static String diagnoseInsert(IAVLNode where) 
	 * diagnoses if there is a problem with where's height after insertion
	 * returns a String with the problem's type
	 */
	private static String diagnoseInsert(IAVLNode where) {
		if (where == null) {
			return "ALL_GOOD";
		}
		int rightDiff = where.getHeight()-where.getRight().getHeight();
		int leftDiff = where.getHeight()-where.getLeft().getHeight();
		if (rightDiff != 0 && leftDiff!=0) {
			return ("ALL_GOOD");
		}
		if ((leftDiff == 1 && rightDiff == 0) || (leftDiff == 0 && rightDiff == 1)) {
			return ("case1");
		}
		if (leftDiff == 2 && rightDiff == 0){
			int rightRightDiff = where.getRight().getHeight() - where.getRight().getRight().getHeight();
			int rightLeftDiff = where.getRight().getHeight() - where.getRight().getLeft().getHeight();
			if (rightLeftDiff == 2 && rightRightDiff == 1) {
				return ("case2right");
			} 
			if (rightLeftDiff == 1 && rightRightDiff == 2) {
				return ("case3right");
			} else {
				return ("case4right");
			}
		}
		if (leftDiff == 0 && rightDiff == 2){
			int leftLeftDiff = where.getLeft().getHeight() - where.getLeft().getLeft().getHeight();
			int leftRightDiff = where.getLeft().getHeight() - where.getLeft().getRight().getHeight();
			if (leftLeftDiff == 1 && leftRightDiff ==2) {
				return ("case2left");
			} 
			if ((leftLeftDiff == 2 && leftRightDiff ==1)) {
				return ("case3left");
			} else {
				return ("case4left");
			}
		}
		return null;
	}

	/**
	 * 	private int rebalanceInsert(IAVLNode curr) 
	 * rebalances the tree after insertion
	 * returns the number of rabalance operations needed
	 */
	private int rebalanceInsert(IAVLNode curr) {
		String diagnose = diagnoseInsert(curr);
		int cnt = 0;
		while (!(diagnose.equals("ALL_GOOD"))) {
			if (diagnose =="case1") {
				fixHeight(curr);
				cnt++;
			}
			if (diagnose == "case2left") {
				rotateRight(curr,curr.getLeft());
				fixHeight(curr);
				cnt += 2;
			}
			if (diagnose == "case2right") {
				rotateLeft(curr,curr.getRight());
				fixHeight(curr);
				cnt +=2;
			}
			if (diagnose == "case3left") {
				rotateLeft(curr.getLeft(),curr.getLeft().getRight());
				rotateRight(curr,curr.getLeft());
				fixHeight(curr);
				fixHeight(curr.getParent().getLeft());
				fixHeight(curr.getParent());
				cnt += 5;				
			}
			if (diagnose == "case3right") {
				rotateRight(curr.getRight(),curr.getRight().getLeft());
				rotateLeft(curr,curr.getRight());
				fixHeight(curr);
				fixHeight(curr.getParent().getRight());
				fixHeight(curr.getParent());
				cnt += 5;				
			}
			if (diagnose == "case4left") {
				rotateRight(curr, curr.getLeft());
				cnt ++;
			}
			if (diagnose == "case4right") {
				rotateLeft(curr, curr.getRight());
				cnt ++;
			}
			curr = curr.getParent();
			diagnose = diagnoseInsert(curr);
		}
		return cnt;
	}

	/**
	 * 	private IAVLNode searchNode(int k) 
	 * finds the node with key k
	 * returns an external leaf if it doesn't exist 
	 */
	private IAVLNode searchNode(int k) {
		IAVLNode curr = this.root;
		while (curr.getKey() != -1) {
			int currKey = curr.getKey(); 
			if (currKey == k) {
				return curr;
			}
			if (currKey > k) {
				curr = curr.getLeft();
			} else {
				curr = curr.getRight();
			}
		}
		return EXTERNAL;		
	}

	/**
	 * 	private IAVLNode deleteForReal(IAVLNode where) 
	 * executes the deletion of where from the tree
	 * returns the parent of the deleted node
	*/
	private IAVLNode deleteForReal(IAVLNode where) {
		IAVLNode parent = where.getParent();
		if (where.getHeight() == 0) { //where is a leaf
			if (parent.getRight() == where) {
				parent.setRight(EXTERNAL);
			} else {
				parent.setLeft(EXTERNAL);
			}
			return parent;
		}
		if (where.getLeft().getHeight() == -1) { //where's left child is an external leaf
			if (parent == null) {
				this.root = where.getRight();
				this.root.setParent(null);
				return null;
			}
			if (parent.getRight() == where) {
				parent.setRight(where.getRight());
			} else {
				parent.setLeft(where.getRight());
			}
			where.getRight().setParent(parent);
			return parent;
		}
		if (where.getRight().getHeight() == -1) { //where's right child is an external leaf
			if(parent == null) {
				this.root = where.getLeft();
				this.root.setParent(null);
				return null;
			}
			if (parent.getRight() == where) {
				parent.setRight(where.getLeft());
			} else {
				parent.setLeft(where.getLeft());
			}
			where.getLeft().setParent(parent);
			return parent;
		}
		IAVLNode successor = successor(where); //where has two children
		IAVLNode successorParent = successor.getParent();
		if (where != successorParent) { //successor is where's right child
			where.getRight().setParent(successor);
			successorParent.setLeft(successor.getRight());
			successor.getRight().setParent(successorParent);
			successor.setRight(where.getRight());
		}
		where.getLeft().setParent(successor);
		successor.setLeft(where.getLeft());
		successor.setHeight(where.getHeight());
		successor.setSize(where.getSize()-1);
		successor.setParent(parent);

		if (parent == null) { //deleting the root
			this.root = successor;
		} else {
			if (parent.getRight() == where) {
				parent.setRight(successor);
			} else {
				parent.setLeft(successor);
			}
		}
		if (where == successorParent) { //successor is where's right child
			return successor;
		}
		return successorParent;
	}

	/**
	 * private IAVLNode successor(IAVLNode curr) 
	 * returns the successor of a node curr - 
	 * the node with the minimal key that is bigger of curr's key
	 */
	private IAVLNode successor(IAVLNode curr) {
		if (curr.getRight().getHeight() != -1) {
			curr = curr.getRight();
			while (curr.getLeft().getHeight() != -1) {
				curr = curr.getLeft();
			}
			return curr;
		} else {
			IAVLNode parent = curr.getParent();
			while (parent != null && parent.getRight() == curr) {
				curr = parent;
				parent = curr.getParent();
			}
			return parent;
		}
	}

	/**
	 * 	private IAVLNode predecessor(IAVLNode curr) 
	 * returns the predecessor of a node curr - 
	 * the node with the maximal key that is smaller of curr's key
	 */
	private IAVLNode predecessor(IAVLNode curr) {
		if (curr.getLeft().getHeight() != -1) {
			curr = curr.getLeft();
			while (curr.getRight().getHeight() != -1) {
				curr = curr.getRight();
			}
			return curr;
		} else {
			IAVLNode parent = curr.getParent();
			while (parent != null && parent.getLeft() == curr) {
				curr = parent;
				parent = curr.getParent();
			}
			return parent;
		}

	}

	/**
	 * 	private static String diagnoseDelete(IAVLNode curr) 
	 * diagnoses if there is a problem with where's height after deletion
	 * returns a String of the problem's type
	 */
	private static String diagnoseDelete(IAVLNode curr) {
		if (curr == null) {
			return "ALL_GOOD";
		}
		int rightDiff = curr.getHeight()-curr.getRight().getHeight();
		int leftDiff = curr.getHeight()-curr.getLeft().getHeight();
		if (rightDiff == 2 && leftDiff == 2) {
			return "case1";			
		}
		if (leftDiff == 3 && rightDiff == 1) {
			int rightLeftDiff = curr.getRight().getHeight()-curr.getRight().getLeft().getHeight();
			int rightRightDiff = curr.getRight().getHeight()-curr.getRight().getRight().getHeight();
			if (rightLeftDiff == 1 && rightRightDiff == 1) {
				return "case2left";
			}
			if (rightLeftDiff == 2 && rightRightDiff == 1) {
				return "case3left";
			}
			if (rightLeftDiff == 1 && rightRightDiff == 2) {
				return "case4left";
			}	
		}
		if (leftDiff == 1 && rightDiff == 3) {
			int leftLeftDiff = curr.getLeft().getHeight()-curr.getLeft().getLeft().getHeight();
			int leftRightDiff = curr.getLeft().getHeight()-curr.getLeft().getRight().getHeight();
			if (leftLeftDiff == 1 && leftRightDiff == 1) {
				return "case2right";
			}
			if (leftLeftDiff == 1 && leftRightDiff == 2) {
				return "case3right";
			}
			if (leftLeftDiff == 2 && leftRightDiff == 1) {
				return "case4right";
			}	
		}
		return "ALL_GOOD";
	}

	/** 
	 * 	private int rebalanceDelete(IAVLNode curr) 
	 * rebalnces the tree after deletion
	 * returns the number of rebalance operations needed
	 */
	private int rebalanceDelete(IAVLNode curr) {
		String diagnose = diagnoseDelete(curr);
		int cnt = 0;
		while (!(diagnose.equals("ALL_GOOD"))) {
			if (diagnose.equals("case1")) {
				fixHeight(curr);
				cnt++;
				curr = curr.getParent();
			}
			if (diagnose.equals("case2left")) {
				rotateLeft(curr, curr.getRight());
				fixHeight(curr);
				fixHeight(curr.getParent());
				cnt += 3;
				curr = curr.getParent().getParent();
			}
			if (diagnose.equals("case2right")) {
				rotateRight(curr, curr.getLeft());
				fixHeight(curr);
				fixHeight(curr.getParent());
				cnt += 3;
				curr = curr.getParent().getParent();
			}
			if (diagnose.equals("case3left")) {
				rotateLeft(curr, curr.getRight());
				fixHeight(curr);
				cnt += 3;
				curr = curr.getParent().getParent();
			}
			if (diagnose.equals("case3right")) {
				rotateRight(curr, curr.getLeft());
				fixHeight(curr);
				cnt += 3;
				curr = curr.getParent().getParent();
			}
			if (diagnose.equals("case4left")) {
				IAVLNode right = curr.getRight();
				IAVLNode rightLeft = right.getLeft();
				rotateRight(right, rightLeft);
				rotateLeft(curr, rightLeft);
				fixHeight(curr);
				fixHeight(right);
				fixHeight(rightLeft);
				cnt += 6;
				curr = curr.getParent().getParent();
			}
			if (diagnose.equals("case4right")) {
				IAVLNode left = curr.getLeft();
				IAVLNode leftRight = left.getRight();
				rotateLeft(left, leftRight);
				rotateRight(curr, leftRight);
				fixHeight(curr);
				fixHeight(left);
				fixHeight(leftRight);
				cnt += 6;
				curr = curr.getParent().getParent();
			}
			diagnose = diagnoseDelete(curr);
		}
		return cnt;
	}
	
	/** 
	 * 	private void fixMinMaxInsert(IAVLNode cand) 
	 * fixes min and max pointers after insertion if needed
	*/
	private void fixMinMaxInsert(IAVLNode cand) {
		if (cand.getKey() < this.min.getKey()) {
			this.min = cand;
		}
		if (cand.getKey() > this.max.getKey()) {
			this.max = cand;
		}
	}

	/** 
	 * 	private void fixMinMaxDelete(int key) 
	 * fixes min and max pointers after deletion if needed
	*/
	private void fixMinMaxDelete(int key) {
		if (this.min.getKey() == key) {
			this.min = successor(this.min);
		}
		if (this.max.getKey() == key) {
			this.max = predecessor(this.max);
		}
	}

	/**
	 * 	private static IAVLNode searchOnRight(AVLTree tree, int a) 
	 * returns the highest node with height <= a on the right side of tree
	 */
	private static IAVLNode searchOnRight(AVLTree tree, int a) {
		IAVLNode curr = tree.root;
		while (curr.getHeight() > a) {
			curr = curr.getRight();
		}
		return curr;
	}

	/**
	 * private static IAVLNode searchOnLeft(AVLTree tree, int a)
	 * returns the highest node with height <= a on the left side of tree
	 */
	private static IAVLNode searchOnLeft(AVLTree tree, int a) {
		IAVLNode curr = tree.root;
		while (curr.getHeight() > a) {
			curr = curr.getLeft();
		}
		return curr;
	}
	
	/** 
	 * private void joinEquals(AVLTree smaller, IAVLNode x, AVLTree bigger)
	 * joins 2 trees of the same height 
	 *  the root of the new tree will be node x
	 */
	private void joinEquals(AVLTree smaller, IAVLNode x, AVLTree bigger) {
		x.setLeft(smaller.root);
		x.setRight(bigger.root);
		smaller.root.setParent(x);
		bigger.root.setParent(x);
		fixHeight(x);
		fixSizeNode(x);
		this.root = x;
		this.size = x.getSize();
		this.min = smaller.min;
		this.max = bigger.max;
	}
	
	/** 
	 * 	private void joinForReal(IAVLNode x, IAVLNode b, IAVLNode a) 
	 * joins 2 trees of different height in the correct place
	 * the joining node is x
	 */
	private void joinForReal(IAVLNode x, IAVLNode b, IAVLNode a) {
		IAVLNode c = b.getParent();
		if (c.getRight() == b) {
			x.setLeft(b);
			x.setRight(a);
			c.setRight(x);
		} else {
			x.setRight(b);
			x.setLeft(a);
			c.setLeft(x);
		}
		b.setParent(x);
		a.setParent(x);
		x.setParent(c);
		fixHeight(x);
		fixSizeNode(x);
		fixSizeNode(c);
	}

	/** 
	 * private IAVLNode findMin()
	 * returns the node with the minimal key in the tree
	 */
	private IAVLNode findMin() {
		IAVLNode curr = this.root;
		while(curr.getLeft().getHeight() != -1) {
			curr = curr.getLeft();
		}
		return curr;
	}
	
	/** 
	 * 	private IAVLNode findMax() 
	 * returns the node with the maximal key in the tree
	 */
	private IAVLNode findMax() {
		IAVLNode curr =  this.root;
		while(curr.getRight().getHeight() != -1) {
			curr =  curr.getRight();
		}
		return curr;
	}
	
	/** 
	 * 	private void fixSizeTree(IAVLNode curr, int i)
	 * adds i to the sizes of the nodes from node curr to the root
	 */
	private void fixSizeTree(IAVLNode curr, int i) {
		while(curr != null) {
			curr.setSize(curr.getSize() + i);
			curr = curr.getParent();
		}
	}


	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode {	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		public void setSize(int size); // sets the size of the node
		public int getSize(); // Returns the size of the node (0 for virtual nodes) 
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in 
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode{

		private int key;
		private String info;
		private IAVLNode right;	  
		private IAVLNode left;
		private IAVLNode parent;
		private int height; 
		private int size; 

		public AVLNode(int key, String info) {
			this.key = key;
			this.info = info;
			this.parent = null;
			if (key == -1) {
				this.height = -1;
				this.right = null;
				this.left = null;
				this.size = 0;
			} else {
				this.height = 0;
				this.right = EXTERNAL;
				this.left = EXTERNAL;
				this.size = 1;
			}
		}

		public int getKey() {
			return this.key;
		}

		public String getValue() {
			return this.info;
		}

		public void setLeft(IAVLNode node) {
			this.left = node;
			return; 
		}

		public IAVLNode getLeft() {
			return this.left; 
		}

		public void setRight(IAVLNode node) {
			this.right = node;
			return; 
		}

		public IAVLNode getRight() {
			return this.right; 
		}

		public void setParent(IAVLNode node) {
			this.parent = node;
			return; 
		}

		public IAVLNode getParent() {
			return this.parent; 
		}

		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode() {
			return (key != -1); 
		}

		public void setHeight(int height) {
			this.height = height;
			return;
		}

		public int getHeight() {
			return this.height;
		}
		
		public void setSize(int size) {
			this.size = size;
			return;
		}
		
		// Returns 0 if this is a virtual AVL node
		public int getSize() {
			return this.size;
		}
	}

}