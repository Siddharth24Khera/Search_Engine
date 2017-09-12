public class AVLtree { 
    private Node root; 
    private PageEntry pEntry;
    private WordEntry wEntry;
    private class Node {
        private Position pos;
        private int balance;
        private int key;
        private Node left, right, parent; 
        Node(Position ps, Node p) {
            pos=ps;
            key = ps.getWordIndex();
            parent = p;
        }
    } 
    public AVLtree(PageEntry p,WordEntry w){
        pEntry=p;
        wEntry=w;
    }
    public PageEntry getPageEntry(){
        return pEntry;
    }
    public WordEntry getWordEntry(){
        return wEntry;
    }
    public boolean insert(Position pos) {
        int key= pos.getWordIndex();
        if (root == null)
            root = new Node(pos, null);
        else {
            Node n = root;
            Node parent;
            while (true){
                if (n.key ==key )
                    return false; 
                parent = n; 
                boolean goLeft = (n.key > key);
                if(goLeft) n=n.left; else n=n.right;
                if (n == null) {
                    if (goLeft) {
                        parent.left = new Node(pos, parent);
                    } else {
                        parent.right = new Node(pos, parent);
                    }
                    rebalance(parent);
                    break;
                }
            }
        }
        return true;
    }
 
    public boolean contains(Position pos) {
        int key= pos.getWordIndex();
        Node n = root;
        Node parent;
        while (true){
           if (n.key ==key )
              return true; 
           parent = n; 
           boolean goLeft = n.key > key;
           n = goLeft ? n.left : n.right; 
           if (n == null) {
              return false;
           }
        }
    }
    public void delete(Position p) {
        int delKey=p.getWordIndex();
        if (root == null)
            return;
        Node n = root;
        Node parent = root;
        Node delNode = null;
        Node child = root;
 
        while (child != null) {
            parent = n;
            n = child;
            if(delKey >= n.key) child=n.right; else child=n.left;
            if (delKey == n.key)
                delNode = n;
        }
 
        if (delNode != null) {
            delNode.key = n.key; 
            child = n.left != null ? n.left : n.right;
            if(n.left != null) child=n.left; else child=n.right;
            if (root.key == delKey) {
                root = child;
            } else {
                if (parent.left == n) {
                    parent.left = child;
                } else {
                    parent.right = child;
                }
                rebalance(parent);
            }
        }
    }
 
    private void rebalance(Node n) {
        setBalance(n); 
        if (n.balance == -2) {
            if (height(n.left.left) >= height(n.left.right))
                n = rotateRight(n);
            else
                n = rotateLeftThenRight(n); 
        } else if (n.balance == 2) {
            if (height(n.right.right) >= height(n.right.left))
                n = rotateLeft(n);
            else
                n = rotateRightThenLeft(n);
        }
 
        if (n.parent != null) {
            rebalance(n.parent);
        } else {
            root = n;
        }
    }
 
    private Node rotateLeft(Node a) { 
        Node b = a.right;
        b.parent = a.parent; 
        a.right = b.left; 
        if (a.right != null)
            a.right.parent = a; 
        b.left = a;
        a.parent = b; 
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        } 
        setBalance(a);
        setBalance(b);
        return b;
    }
 
    private Node rotateRight(Node a) { 
        Node b = a.left;
        b.parent = a.parent; 
        a.left = b.right; 
        if (a.left != null)
            a.left.parent = a; 
        b.right = a;
        a.parent = b; 
        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        } 
        setBalance(a); 
        setBalance(b);
        return b;
    }
 
    private Node rotateLeftThenRight(Node n) {
        n.left = rotateLeft(n.left);
        return rotateRight(n);
    }
 
    private Node rotateRightThenLeft(Node n) {
        n.right = rotateRight(n.right);
        return rotateLeft(n);
    }
 
    private int height(Node n) {
        if (n == null)
            return -1;
        return 1 + Math.max(height(n.left), height(n.right));
    }
 
    private void setBalance(Node n) {
        n.balance = height(n.right) - height(n.left);
    }

    public void inOrder(Node n,MyLinkedList<Position> ll) {        
        if (n != null) {
            inOrder(n.left,ll);
            ll.addFirst(n.pos);
            inOrder(n.right,ll);
        }
    }
    public MyLinkedList<Position> makePosList(){
        MyLinkedList<Position> ll=new MyLinkedList<Position>();
        inOrder(root,ll);
        return ll;
    }
}