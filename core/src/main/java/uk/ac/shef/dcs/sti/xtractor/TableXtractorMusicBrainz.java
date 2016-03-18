package uk.ac.shef.dcs.sti.xtractor;

import org.apache.any23.extractor.html.DomUtils;
import org.apache.any23.extractor.html.TagSoupParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import uk.ac.shef.dcs.sti.STIException;
import uk.ac.shef.dcs.sti.rep.Table;
import uk.ac.shef.dcs.sti.rep.LTableContext;
import uk.ac.shef.dcs.sti.xtractor.validator.TableValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zqz
 * Date: 20/02/14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class TableXtractorMusicBrainz extends TableXtractor{
    public TableXtractorMusicBrainz(TableNormalizer normalizer, TableHODetector detector, TableObjCreator creator, TableValidator... validators) {
        super(normalizer, detector, creator, validators);
    }

    @Override
    public List<Table> extract(String input, String sourceId) {
        List<Table> rs = new ArrayList<Table>();
        parser = new TagSoupParser(new ByteArrayInputStream(input.getBytes()), sourceId,"UTF-8");
        Document doc = null;
        try {
            doc = parser.getDOM();
        } catch (IOException e) {
            return rs;
        }

        List<Node> tables = DomUtils.findAll(doc, "//TABLE[@class='tbl']");
        List<LTableContext> contexts = new ArrayList<LTableContext>();
        try {
            contexts = Table_ContextExtractor_MusicBrainz.extractTableContexts(sourceId,doc);
        } catch (STIException e) {
            e.printStackTrace();
        }
        int tableCount = 0;
        for (Node n : tables) {
            tableCount++;

            LTableContext[] contexts_array = new LTableContext[contexts.size()];
            for (int i = 0; i < contexts.size(); i++)
                contexts_array[i] = contexts.get(i);
            Table table = extractTable(n, String.valueOf(tableCount),
                    sourceId, contexts_array);
            if (table != null)
                rs.add(table);

        }
        return rs;
    }
}