import java.util.*;
import java.io.*;

public class SearchEngine{
    static InvertedPageIndex IPIndex;
    MySort sorter;
    public SearchEngine() {
        IPIndex = new InvertedPageIndex();
        sorter = new MySort();
    }
    public void performAction(String actionMessage) {
        String[] tokens = actionMessage.split("\\s");
        if(tokens[0].equals("addPage")){      
            PageEntry p = new PageEntry(tokens[1]);
            IPIndex.addPage(p);
          }
        if(tokens[0].equals("queryFindPagesWhichContainWord")){
            tokens[1]=tokens[1].toLowerCase();
            if(tokens[1].equals("stacks")) tokens[1] = new String("stack");
            if(tokens[1].equals("structures")) tokens[1] = new String("structure");
            if(tokens[1].equals("applications")) tokens[1] = new String("application");
            if((IPIndex.getPagesWhichContainWord(tokens[1]))==null){ System.out.println("No webpage contain word "+ tokens[1].toLowerCase()); return;}
            MyLinkedList<PageEntry> pageEntryList= IPIndex.getPagesWhichContainWord(tokens[1].toLowerCase()).ll;
            pageEntryList.resetParser();
            int sizeOfList = pageEntryList.getSize();
            int count=0;
            PageEntry temp;
            while(pageEntryList.hasElement()){
                temp= pageEntryList.peekAndNext();
                count++;
                if(count==sizeOfList){
                    System.out.println(temp.getPageName());
                    return;
                }
                System.out.print(temp.getPageName()+",");              
              }
          }
        if(tokens[0].equals("queryFindPositionsOfWordInAPage")){
            String wordName = tokens[1].toLowerCase();
            String pageName = tokens[2];
            WordEntry w = IPIndex.getWordEntry(wordName);
            if(w==null){
                System.out.println("Webpage "+ tokens[2] +" does not contain word "+wordName); 
                return;
            }
            MyLinkedList<Position> posList = w.getAllPositionsForThisWord();
            posList.resetParser();
            boolean flag=false;
            Position p;
            MyLinkedList<Integer> listOfIndex = new MyLinkedList<Integer>();
            while(posList.hasElement()){
                p=posList.peekAndNext();
                String pName = p.getPageEntry().getPageName();
                if(pName.equals(tokens[2])){
                    listOfIndex.addFirst(p.getWordIndex());
                }
            }
            int sizeOfList = listOfIndex.getSize();
            if(sizeOfList == 0){
                System.out.println("Webpage "+ tokens[2] +" does not contain word "+wordName); 
                return;
            }
            int count = 0;
            int temp;
            listOfIndex.reverse();
            listOfIndex.resetParser();
            while(listOfIndex.hasElement()){
                temp= listOfIndex.peekAndNext();
                count++;
                if(count==sizeOfList){
                    System.out.println(temp);
                    return;
                }
                System.out.print(temp+",");              
              }
        }
        if(tokens[0].equals("queryFindPagesWhichContainAllWords")){
            int i;
            MySet<SearchResult> searchSet=new MySet<SearchResult>();
            MySet<PageEntry> intersectSet = new MySet<PageEntry>();
            for(i=1;i<tokens.length;i++){
                tokens[i]=tokens[i].toLowerCase();
                if(tokens[i].equals("stacks")) tokens[i] = new String("stack");
                if(tokens[i].equals("structures")) tokens[i] = new String("structure");
                if(tokens[i].equals("applications")) tokens[i] = new String("application");
                if((IPIndex.getPagesWhichContainWord(tokens[i]))==null){System.out.println("One of the words not contained in any webpage");return; }
                MySet<PageEntry> mset =  IPIndex.getPagesWhichContainWord(tokens[i]);
                if(i==1) intersectSet=intersectSet.Union(mset);
                else intersectSet=intersectSet.Intersection(mset);
            }
            if(intersectSet.getSize() == 0) {System.out.println("No webpage contain all these words");return; }
            MyLinkedList<PageEntry> pEntryList = intersectSet.ll;
            pEntryList.resetParser();
            PageEntry pEntry;
            String[] strArray = Arrays.copyOfRange(tokens,1,tokens.length);
            while(pEntryList.hasElement()){
                pEntry = pEntryList.peekAndNext();
                SearchResult sr = new SearchResult(pEntry,pEntry.getRelevanceOfPage(strArray,false));
                searchSet.addElement(sr);
            }
            ArrayList<SearchResult> list = sorter.sortThisList(searchSet);
            int siz = list.size();
            for(i=0;i<siz-1;i++){
                System.out.print(list.get(i).getPageEntry().getPageName()+ ",");
            }
            System.out.print(list.get(i).getPageEntry().getPageName());
            System.out.println();
        }
        if(tokens[0].equals("queryFindPagesWhichContainAnyOfTheseWords")){
            int i;
            MySet<SearchResult> searchSet=new MySet<SearchResult>();
            MySet<PageEntry> unionSet = new MySet<PageEntry>();
            for(i=1;i<tokens.length;i++){
                tokens[i]=tokens[i].toLowerCase();
                if(tokens[i].equals("stacks")) tokens[i] = new String("stack");
                if(tokens[i].equals("structures")) tokens[i] = new String("structure");
                if(tokens[i].equals("applications")) tokens[i] = new String("application");
                if((IPIndex.getPagesWhichContainWord(tokens[i]))==null){continue;}
                MySet<PageEntry> mset =  IPIndex.getPagesWhichContainWord(tokens[i]);
                unionSet=unionSet.Union(mset);
            }
            if(unionSet.getSize() == 0) {System.out.println("No webpage contain any of these words");return; }
            MyLinkedList<PageEntry> pEntryList = unionSet.ll;
            pEntryList.resetParser();
            PageEntry pEntry;
            String[] strArray = Arrays.copyOfRange(tokens,1,tokens.length);
            while(pEntryList.hasElement()){
                pEntry = pEntryList.peekAndNext();
                SearchResult sr = new SearchResult(pEntry,pEntry.getRelevanceOfPage(strArray,false));
                searchSet.addElement(sr);
            }
            ArrayList<SearchResult> list = sorter.sortThisList(searchSet);
            int siz = list.size();
            for(i=0;i<siz-1;i++){
                System.out.print(list.get(i).getPageEntry().getPageName() + ",");
            }
            System.out.print(list.get(i).getPageEntry().getPageName());
            System.out.println();
        }
        if(tokens[0].equals("queryFindPagesWhichContainPhrase")){
            String[] strArray = Arrays.copyOfRange(tokens,1,tokens.length);
            MySet<PageEntry> pageSet= IPIndex.getPagesWhichContainPhrase(strArray);
            if(pageSet == null){System.out.println("No page contain this phrase"); return;}
            MyLinkedList<PageEntry> list = pageSet.ll;
            list.resetParser();
            PageEntry p;
            int i;
            MySet<SearchResult> searchSet = new MySet<>();
            while(list.hasElement()){
                p = list.peekAndNext();
                SearchResult sr = new SearchResult(p,p.getRelevanceForPhrase(strArray.length));
                p.resetNum();
                searchSet.addElement(sr);
            }
            ArrayList<SearchResult> liste = sorter.sortThisList(searchSet);
            int siz = liste.size();
            for(i=0;i<siz-1;i++){
                System.out.print(liste.get(i).getPageEntry().getPageName()+",");
            }
            System.out.print(liste.get(i).getPageEntry().getPageName());
            System.out.println();
        }
    }
}

class MySet<E>
{
    public MyLinkedList<E> ll;
    private int size;    
    public MySet()
    {
        ll = new MyLinkedList<E>();
        size=0;
    }    
    public boolean IsEmpty()
    {
        return size==0;
    }    
    public boolean IsMember(E o)
    {
        int t = ll.indexOf(o);
        if(t==-1)
            return false;
        return true;
    }   
    public void addElement(E o)
    {
        if(!IsMember(o)){
            ll.addFirst(o); 
            size++;
        }
    }    
    public int getSize(){
        return size;
    }    
    public void Delete(E o) throws IllegalArgumentException
    { 
        ll.remove(o);
        size--;
    }    
    public MySet<E> Intersection(MySet<E> a)
    {
        E temp;
        MySet<E> inter = new MySet<E>();
        ll.resetParser();
        a.ll.resetParser();
        while(ll.hasElement()){
            temp=ll.peekAndNext();
            if(a.IsMember(temp)){
                inter.addElement(temp);
            }              
        }  
        return inter;        
    }    
    public MySet<E> Union(MySet<E> a)
    {
        E temp;
        MySet<E> union = new MySet<E>();
        ll.resetParser();
        a.ll.resetParser();
        while(ll.hasElement()){
            temp=ll.peekAndNext();
            union.addElement(temp);                
        }     
        MyLinkedList<E> y = a.ll;
        while(y.hasElement()){
            temp=y.peekAndNext();
            if(!union.IsMember(temp))
              union.addElement(temp);
        }  
        return union;
    }   
}

class MyLinkedList<E>
{
    class Node{
        public E element;
        public Node next;        
        public Node(E o){  
            element= o;
            next=null;
        }
    }
    private Node head;
    private Node parser;
    private int size;
    public MyLinkedList(){
        head=null;
        parser=null;
        size=0;
    }
    public void reverse() {
        Node p = null;
        Node current = head;
        Node n = null;
        while (current != null) {
            n = current.next;
            current.next = p;
            p = current;
            current = n;
        }
        head=p;
        return;
    }
    public void addFirst(E o){
        Node n = new Node(o);
        n.next=head;
        head=n;
        size++;
    }
    public int indexOf(E o){
        resetParser();
        int count=0;
        while(parser!=null){
            if(parser.element==o) break;
            count++;
            parser=parser.next;
        }
        if(parser==null) return -1;
        return count;
    }
    public void resetParser(){
        parser=head;
    }
    public void remove(E o)  throws IllegalArgumentException 
    {
        resetParser();
        Node preParser=null;
        if(parser.element==o){
            head=head.next;
            return;
        }
        while(parser!=null){
            if(parser.element==o) break;
            preParser=parser;
            parser=parser.next;
        }
        if(parser==null) throw new IllegalArgumentException("Entry not present");
        preParser.next=parser.next;
    }    
    public boolean hasElement(){
        return !(parser==null);
    }
    public E peekAndNext(){
        E temp= parser.element;
        parser=parser.next;
        return temp;
    }    
    public int getSize(){return size;}
}

class Position{
    private PageEntry pEntry;
    private int wIndex;
    public Position(PageEntry p,int wordIndex){
        pEntry=p;
        wIndex= wordIndex;
    }
    public PageEntry getPageEntry(){return pEntry;}
    public int getWordIndex(){return wIndex;}
}

class WordEntry{
    private String word;
    private MyLinkedList<AVLtree> avtreeList;
    private MyLinkedList<Position> ll;
    public WordEntry(String wrd){
        word=wrd;
        ll= new MyLinkedList <Position>();  
        avtreeList= new MyLinkedList<AVLtree>();
    }
    public void addPosition(Position p){
        if(avtreeList.getSize() ==0){
           AVLtree avtree = new AVLtree(p.getPageEntry(),this);
           avtreeList.addFirst(avtree);
           avtree.insert(p);
        }
        boolean flag= false;
        avtreeList.resetParser();
        AVLtree temp;
        while(avtreeList.hasElement()){
            temp= avtreeList.peekAndNext();
            if(temp.getPageEntry()==p.getPageEntry()){
                temp.insert(p); flag=true; break;
            }
        }
        if(flag==false){
           AVLtree avtree = new AVLtree(p.getPageEntry(),this);
           avtreeList.addFirst(avtree);
           avtree.insert(p);
        }
        ll.addFirst(p);
    }
    public String getString(){
        return word;
    }
    public void addPositions(MyLinkedList<Position> positions){
        Position temp;
        positions.resetParser();
        while(positions.hasElement()){
            temp=positions.peekAndNext();
            if(avtreeList.getSize() ==0){
                AVLtree avtree = new AVLtree(temp.getPageEntry(),this);
                avtreeList.addFirst(avtree);
                avtree.insert(temp);
            }
            boolean flag= false;
            avtreeList.resetParser();
            AVLtree tempTree;
            while(avtreeList.hasElement()){
                tempTree= avtreeList.peekAndNext();
                if(tempTree.getPageEntry()==temp.getPageEntry()){
                    tempTree.insert(temp); flag=true; break;
                }
            }
            if(flag==false){
                AVLtree avtree = new AVLtree(temp.getPageEntry(),this);
                avtreeList.addFirst(avtree);
                avtree.insert(temp);
            }
            if(ll.indexOf(temp)==-1){
                ll.addFirst(temp);
            }
        }
    }
    public MyLinkedList<Position> getAllPositionsForThisWord(){
        return ll;
    }
    public AVLtree getTreeForAllPositionsOnPage(PageEntry p){
        avtreeList.resetParser();
        AVLtree temp;
        while(avtreeList.hasElement()){
            temp= avtreeList.peekAndNext();
            if(temp.getPageEntry()==p) return temp;
        }
        return null;
    }
}

class PageIndex{
    private WordEntryList ll;
    private int numEntry;
    public PageIndex(){
        ll=new WordEntryList();
        numEntry =0;
    }
    void addPositionForWord(String str,Position p){
        ll.resetParser();
        WordEntry temp=null;
        boolean flag=false;         
        while(ll.hasElement()){
            temp=ll.peekAndNext();
            if(temp.getString().equals(str)){
                flag=true;
                break;
             }          
          }
        if(flag==false){
            WordEntry w = new WordEntry(str);           
            w.addPosition(p);
            ll.addFirst(w);        
            numEntry++;
            return;
        }
        temp.addPosition(p);
    }    
    public WordEntryList getListOfWordEntries(){
        return ll;
    }
}

class PageEntry{
    private PageIndex pgInd;
    private String pageName;
    public ArrayList<Integer> num;
    public PageEntry(String pname){
        pageName = pname;
        pgInd = new PageIndex();
        num=new ArrayList<>();
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader("webpages/"+pageName));
            int count=0;
            while ((line = br.readLine()) != null) {
                if(line.equals(""))continue;
                line=line.replaceAll("[-_<>={}(),#!':;.?]"," ");
                String[] tokens = line.split("\\s+");
                int i;
                for(i=0;i<tokens.length;i++){
                    tokens[i]=tokens[i].replaceAll("[^+A-Za-z0-9]+","");
                    tokens[i]=tokens[i].toLowerCase();
                    if(tokens[i].equals("stacks")) tokens[i] = new String("stack");
                    if(tokens[i].equals("structures")) tokens[i] = new String("structure");
                    if(tokens[i].equals("applications")) tokens[i] = new String("application");
                    count++;
                    if(tokens[i].equals("a") || tokens[i].equals("an") || tokens[i].equals("the") 
                        ||tokens[i].equals("they") || tokens[i].equals("these") || 
                            tokens[i].equals("this") || tokens[i].equals("for") || tokens[i].equals("is")
                         ||tokens[i].equals("are") ||tokens[i].equals("was") ||tokens[i].equals("of") ||
                        tokens[i].equals("or") ||tokens[i].equals("and") ||tokens[i].equals("does") ||
                        tokens[i].equals("will") ||tokens[i].equals("whose"))
                            continue;                           
                    pgInd.addPositionForWord(tokens[i],new Position(this,count));
                }               
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public PageIndex getPageIndex(){ return pgInd;}
    public String getPageName(){return pageName;}
    float getRelevanceOfPage(String str[], boolean doTheseWordsRepresentAPhrase){
        int len = str.length;
        int i;
        float relevance=0.0f;
        if(!doTheseWordsRepresentAPhrase){
            for(i=0;i<len;i++){
                relevance+= calcForWord(str[i]);
            }
        }
        return relevance;
    }
    float calcForWord(String str){
        WordEntryList ll = this.getPageIndex().getListOfWordEntries();
        ll.resetParser();
        WordEntry temp=null;
        float sum =0f;
        boolean flag=false;         
        while(ll.hasElement()){
           temp=ll.peekAndNext();
           if(temp.getString().equals(str)){
               flag=true;
               break;
           }
        } 
        if(flag==false){
            return 0;
        }
        AVLtree avtree = temp.getTreeForAllPositionsOnPage(this);
        if(avtree==null) return 0;
        MyLinkedList<Position> posListForPage = avtree.makePosList();
        Position p ;
        posListForPage.resetParser();
        while(posListForPage.hasElement()){
            p = posListForPage.peekAndNext();
            sum+=1.0/(p.getWordIndex()*p.getWordIndex()*1.0);
        }
        return sum;
    }
    public void addNumber(int i){
        num.add(i);
    }
    public void resetNum(){
        num.clear();
    }
    public float getRelevanceForPhrase(int len){
        float sum=0.0f;
        int i,j;
        for(i=0;i<num.size();i++){
            for(j=0;j<len;j++){
                sum+=1.0/((num.get(i)+j)*(num.get(i)+j)*1.0);
            }
        }
        return sum;
    }
}

class WordEntryList extends MyLinkedList<WordEntry>{
    public WordEntryList(){super();}
}

class MyHashTable{
    WordEntryList[] arrayWordEntryList;
    public MyHashTable(){
        arrayWordEntryList = new WordEntryList[97];
        for(int i=0;i<97;i++){
            arrayWordEntryList[i]=new WordEntryList();
        }
    }
    private int getHashIndex(String str){
        int x = horner((str.toCharArray()),str.length(),3);
        return x % 97;
    }    
    private int horner(char poly[], int n, int x)
    {
        int result = poly[0];// Initialize result  
        for (int i=1; i<n; i++)
            result = result*x + poly[i];
        if(result<0)result=(-1)*result;
        return result;
    }
    void addPositionsForWord(WordEntry w){
        String s= w.getString();
        int index = getHashIndex(s);
        WordEntryList list= arrayWordEntryList[index];
        if(list.getSize() == 0){
           list.addFirst(w);
           return;
        }
        // Search this list unless you find the wordEntry with word 's' & merge it with w
        list.resetParser();
        WordEntry temp;
        boolean flag=false;
        while(list.hasElement()){
            temp= list.peekAndNext();
            if(temp.getString().equals(s)){
                temp.addPositions(w.getAllPositionsForThisWord());
                flag=true;
            }
        }
        if(flag==false){
            list.addFirst(w);
        }
    }
    public MySet<PageEntry> createPageSet(String str){
        MySet<PageEntry> mset = new MySet<PageEntry>();
        WordEntryList list = arrayWordEntryList[getHashIndex(str)];
        if(list.getSize() == 0)  return null;
        list.resetParser();
        WordEntry temp=null;
        boolean flag=false;        
        while(list.hasElement()){
            temp= list.peekAndNext();
            if(temp.getString().equals(str)){
                flag=true;
                break;
            }
        }
        if(flag==false) return null;
        MyLinkedList<Position> posList = temp.getAllPositionsForThisWord();
        Position tempr;
        posList.resetParser();
        while(posList.hasElement()){
            tempr= posList.peekAndNext();
            mset.addElement(tempr.getPageEntry());
        }
        return mset;
    }
    public WordEntry findWordEntry(String str){
        WordEntryList list = arrayWordEntryList[getHashIndex(str)];
        list.resetParser();
        if(list.getSize() == 0)  return null;
        WordEntry temp=null;
        boolean flag=false;        
        while(list.hasElement()){
            temp= list.peekAndNext();
            if(temp.getString().equals(str)){
                flag=true;
                break;
            }
        }
        if(flag==false) return null;
        return temp;
    }
}

class InvertedPageIndex{
    MyHashTable hTab;
    String[] strArray = new String[10];
    public InvertedPageIndex(){
        hTab= new MyHashTable();
    }
    void addPage(PageEntry p){        
        WordEntryList wList= p.getPageIndex().getListOfWordEntries();
        WordEntry temp;
        wList.resetParser();
        while(wList.hasElement()){
            temp= wList.peekAndNext();
            hTab.addPositionsForWord(temp);
        }        
    }
    public MySet<PageEntry> getPagesWhichContainWord(String str){
        return hTab.createPageSet(str);
    }
    public WordEntry getWordEntry(String str){
        return hTab.findWordEntry(str);
    }
    MySet<PageEntry> getPagesWhichContainPhrase(String str[]){
        int i;
        MySet<PageEntry> intersectSet = new MySet<PageEntry>();
        for(i=0;i<str.length;i++){
            str[i]=str[i].toLowerCase();
            if(str[i].equals("stacks")) str[i] = new String("stack");
            if(str[i].equals("structures")) str[i] = new String("structure");
            if(str[i].equals("applications")) str[i] = new String("application");
            if((SearchEngine.IPIndex.getPagesWhichContainWord(str[i]))==null){return null;}
            strArray[i]=str[i];
            MySet<PageEntry> mset =  SearchEngine.IPIndex.getPagesWhichContainWord(str[i]);
            if(i==1) intersectSet=intersectSet.Union(mset);
            else intersectSet=intersectSet.Intersection(mset);
        }
        if(intersectSet.getSize() == 0) {return null; }
        MySet<PageEntry> finalSet = new MySet<PageEntry>();
        int len = str.length;
        MyLinkedList<PageEntry> pEntryList = intersectSet.ll;
        PageEntry pEntry;
        pEntryList.resetParser();
        while(pEntryList.hasElement()){
            boolean flag;
            pEntry = pEntryList.peekAndNext();
            MyLinkedList<Position> posListFor1 = getWordEntry(str[0]).getTreeForAllPositionsOnPage(pEntry).makePosList();
            posListFor1.resetParser();
            Position p1;
            while(posListFor1.hasElement()){
                p1=posListFor1.peekAndNext();
                flag = findNext(0,p1.getWordIndex(),len,pEntry);
                if(flag==true){
                    finalSet.addElement(pEntry);
                    pEntry.addNumber(p1.getWordIndex());
                }
            }
        }
        return finalSet;
    }
    boolean findNext(int i,int key,int len,PageEntry pEntry){
        if(i==len-1) return true;
        MyLinkedList<Position> posListFor = getWordEntry(strArray[i+1]).getTreeForAllPositionsOnPage(pEntry).makePosList();
        posListFor.resetParser();
        boolean check=false;
        Position p;
        while(posListFor.hasElement()){
            p=posListFor.peekAndNext();
            if(p.getWordIndex()==key+1){
                check=findNext(i+1,key+1,len,pEntry);
                break;
            }            
        }
        return check;
    }
}

class SearchResult implements Comparable<SearchResult>{
    private PageEntry pEntry;
    private float relevance;
    public SearchResult(PageEntry p , float r){
        pEntry =p;
        relevance = r;
    }
    public PageEntry getPageEntry(){
        return pEntry;
    }
    public float getRelevance(){
        return relevance;
    }
    public int compareTo(SearchResult otherObject){
        if(this.relevance > otherObject.getRelevance())
            return -1;
        else if(this.relevance == otherObject.getRelevance())
            return 0;
        else return 1;
    }    
}

class MySort{
    ArrayList<SearchResult> sortThisList(MySet<SearchResult> listOfSortableEntries){
        MyLinkedList<SearchResult> srList =listOfSortableEntries.ll;
        srList.resetParser();
        ArrayList<SearchResult> srAList = new ArrayList<>();
        while(srList.hasElement()){
            srAList.add(srList.peekAndNext());
        }
        Collections.sort(srAList);
        return srAList;
    }
}