package collector.domain.apicontainer;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiContainer {

    private String Id;

    private ArrayList<String> Names;

    private String Image;

    private String ImageID;

    private String Command;

    private String Created;

    private ArrayList<String> ports;

    private HashMap<String,String> Labels;

    private String State;

    private String Status;

    public ApiContainer() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public ArrayList<String> getNames() {
        return Names;
    }

    public void setNames(ArrayList<String> names) {
        Names = names;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getImageID() {
        return ImageID;
    }

    public void setImageID(String imageID) {
        ImageID = imageID;
    }

    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public ArrayList<String> getPorts() {
        return ports;
    }

    public void setPorts(ArrayList<String> ports) {
        this.ports = ports;
    }

    public HashMap<String, String> getLabels() {
        return Labels;
    }

    public void setLabels(HashMap<String, String> labels) {
        Labels = labels;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

}
