package ch.skyfy.appautomaticinstaller.contants;

public enum Programmes{

    INTELLIJ_IDEA_ULTIMATE("E:\\Documents\\Resources\\Softwares\\JetBrains\\IntelliJ IDEA\\ideaIU-2019.2.3.win.zip", "C:\\temp\\temp1"),
    INTELLIJ_IDEA_COMMUNITY("E:\\Documents\\Resources\\Softwares\\JetBrains\\IntelliJ IDEA\\ideaIC-2019.2.3.win.zip", "C:\\temp\\temp2"),
    TEST("C:\\Users\\Skyfy16\\Desktop\\test.zip", "C:\\temp\\temp1");

    private String path;
    private String destination;

    Programmes(String path, String destination){
        this.path = path;
        this.destination = destination;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getDestination(){
        return destination;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }
}
