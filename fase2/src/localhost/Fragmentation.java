public class Fragmentation {
    private int dadosLength;
    private int maxSize;
    public Fragmentation(int length,int max){
        this.dadosLength=length;
        this.maxSize = max;
    }
    public int numberofFragments(){
        int i=0;
        int n;
        int rest = this.dadosLength%this.maxSize;
        if(this.dadosLength<this.maxSize) return 1;
        else if(rest==0){
            n=this.dadosLength/this.maxSize;
            return n;
        }
        else{
            while((this.dadosLength/this.maxSize)!=0){
                this.dadosLength = this.dadosLength-(this.maxSize*i);
                i++;
            }
            return i;
        }
    }
}

