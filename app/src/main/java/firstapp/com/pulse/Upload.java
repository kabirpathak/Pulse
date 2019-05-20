package firstapp.com.pulse;

public class Upload {

    private String mName;
    private String mImageUrl;

    public Upload(){

    }

    public Upload(String name, String imageUrl){

        if(name.trim().equals("")){
            name = "No name";
        }

        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName(){
        return mName;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String url){
        mImageUrl = url;
    }

    public void setName(String myName){
        mName = myName;
    }

}



