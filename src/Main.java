
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {

        String json = new String(Files.readAllBytes(Paths.get("home_1.json")));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        String output = getTimesForSegmentEvent(root.get("segment_event"));

        try (FileWriter writer = new FileWriter("home_1.flat.csv")) {
            writer.write(output);
            writer.close();
        }
        System.out.print(output);
    }


    private static String getTimesForSegmentEvent(JsonNode segmentEvent) {

        String output = "" ;
        if (segmentEvent == null) return output;

        JsonNode segment = segmentEvent.get("segment");
        if (segment != null){
            String id = segment.get("_id").textValue();
            String inTime = segment.get("in_time").textValue();
            String outTime = segment.get("out_time").textValue();
            output = String.format("%s, %s, %s \n", id, inTime, outTime);
        }

        JsonNode timeline = segmentEvent.get("timeline");
        if (timeline == null) return output;

        ArrayNode segmentEventPlacements = (ArrayNode) timeline.get("segmentEventPlacements");
        if (segmentEventPlacements !=null) {
            for (int i = 0; i < segmentEventPlacements.size(); i++) {
                JsonNode sgmEvn =segmentEventPlacements.get(i).get("segment_event");
                if (sgmEvn != null ){
                    output = output + getTimesForSegmentEvent(sgmEvn);
                }
            }
        }
        return output;
    }
}
