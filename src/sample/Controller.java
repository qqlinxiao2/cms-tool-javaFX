package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

;

public class Controller {

    @FXML
    private Pane rootLayout;

    @FXML
    private TableView table;

    @FXML
    private Button importBtn;

    @FXML
    private Button executeBtn;

    @FXML
    private TextField carrierId;

    @FXML
    private ChoiceBox<String> platform;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private
    final ObservableList<Data> tableList = FXCollections.observableArrayList();

    private SqlSessionFactory sqlSessionFactory;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'sample.fxml'.";
        initTable();
        initCondition();
        initButton();
        initMybatis();
    }

    private void initCondition() {
        ObservableList<String> list = FXCollections.observableArrayList("华为", "中兴", "烽火");
        platform.setValue("华为");
        platform.setItems(list);
    }

    private void initMybatis() {
        try{
            String resource = "sample/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        }catch (Exception e){
            informationDialog(e.getMessage());
        }
    }

    private void initTable(){
        progressIndicator.setVisible(false);
        table.setEditable(false);
        TableColumn nameCol = new TableColumn("NAME");
        TableColumn codeCol = new TableColumn("CODE");
        TableColumn accessUrlCol = new TableColumn("ACCESS_URL");
        TableColumn cspidCol = new TableColumn("CSPID");
        TableColumn originalCol = new TableColumn("原始");
        TableColumn oneMapingCol = new TableColumn("一次映射");
        TableColumn twoMappingCol = new TableColumn("二次映射");
        TableColumn statusCol = new TableColumn("状态");
        nameCol.setMinWidth(80);
        nameCol.setSortable(false);
        codeCol.setMinWidth(180);
        codeCol.setSortable(false);
        accessUrlCol.setMinWidth(120);
        accessUrlCol.setSortable(false);
        cspidCol.setSortable(false);
        originalCol.setSortable(false);
        oneMapingCol.setSortable(false);
        twoMappingCol.setSortable(false);
        statusCol.setSortable(false);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        accessUrlCol.setCellValueFactory(new PropertyValueFactory<>("accessUrl"));
        cspidCol.setCellValueFactory(new PropertyValueFactory<>("cspid"));
        originalCol.setCellValueFactory(new PropertyValueFactory<>("original"));
        oneMapingCol.setCellValueFactory(new PropertyValueFactory<>("oneMapping"));
        twoMappingCol.setCellValueFactory(new PropertyValueFactory<>("twoMapping"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(nameCol,codeCol,accessUrlCol,cspidCol,originalCol
                ,oneMapingCol,twoMappingCol,statusCol);
        table.setItems(tableList);
    }

    private void initButton() {
        Image image = new Image(getClass().getResourceAsStream("add.png"));
        ImageView imageView = new ImageView(image);
        importBtn.setGraphic(imageView);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        importBtn.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File homeDirectory = fsv.getHomeDirectory();
            fileChooser.setInitialDirectory(homeDirectory);
            fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showOpenDialog(Main.stage);
            if(file != null){
                List<Map<String, String>> list = loadCsv(file);
                loadTable(list);
            }
        });

        image = new Image(getClass().getResourceAsStream("start.png"));
        imageView = new ImageView(image);
        executeBtn.setGraphic(imageView);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        executeBtn.setOnAction((ActionEvent e) -> {
            new Thread(() -> execute()).start();
        });

    }

    private void loadTable(List<Map<String, String>> list){
        tableList.clear();
        for(Map<String,String> map : list){
            Object[] values = map.values().toArray();
            Data data = new Data();
            data.setName((String)values[0]);
            data.setCode((String)values[1]);
            data.setAccessUrl((String)values[2]);
            data.setCspid((String)values[3]);
            tableList.add(data);
        }
    }

    private List<Map<String,String>> loadCsv(File file){
        List<Map<String,String>> data = new ArrayList<Map<String,String>>();
        try{
            CsvReader r = new CsvReader(file,"UTF-8");
            List<String> results = new ArrayList<String>();

            // 校验表头
            String headers = r.readHeaders();
            //this.validateHeaders(headers);

            // 逐条读取记录，直至读完
            while(r.hasNext()){
                //按列名读取这条记录的值
                Map<String,String> rowMap = r.getRowMap();
                //String contentId = this.validateContents(r.getLine(),rowMap);
                data.add(rowMap);
            }

        }catch (Exception e){
            informationDialog(e.getMessage());
        }
        return data;
    }

    public void execute(){
        importBtn.setDisable(true);
        executeBtn.setDisable(true);
        progressIndicator.setVisible(true);
        table.setDisable(true);
        String platformValue = platform.getValue();
        String carrierIdText = carrierId.getText();
        SqlSession sqlSession = null;
        try{
            if(StringUtils.isBlank(carrierIdText)){
                carrierId.setFocusTraversable(true);
                return;
            }
            sqlSession = sqlSessionFactory.openSession();
            OracleMapper mapper = sqlSession.getMapper(OracleMapper.class);

            for(int i = 0; i < tableList.size(); i++){
                Data data = tableList.get(i);
                String code = data.getCode();
                //原始
                String providerCode = mapper.queryProviderByCode(code);
                if(StringUtils.isNotBlank(providerCode)){
                    data.setOriginal(providerCode);
                }
                //一次
                String name = "PROGRAMCODEMAPPING" + carrierIdText;
                String id = mapper.queryContentIdByMapping(name,code);
                if(StringUtils.isNotBlank(id)){
                    providerCode = mapper.queryProviderById(id);
                    if(StringUtils.isNotBlank(providerCode)){
                        data.setOneMapping(providerCode);
                    }
                }
                //二次
                switch (platformValue){
                    case "华为":
                        name = "PROGRAMCODEMAPPING" + carrierIdText+ "HW";
                        break;
                    case "中兴":
                        name = "PROGRAMCODEMAPPING" + carrierIdText+ "ZX";
                        break;
                    case "烽火":
                        name = "PROGRAMCODEMAPPING" + carrierIdText+ "FH";
                        break;
                    default:
                        break;
                }
                id = mapper.queryContentIdByMapping(name,code);
                if(StringUtils.isNotBlank(id)){
                    providerCode = mapper.queryProviderById(id);
                    if(StringUtils.isNotBlank(providerCode)){
                        data.setTwoMapping(providerCode);
                    }
                }
                data.setStatus("finished.");
                table.refresh();
                progressIndicator.setProgress(((double)(i+1))/tableList.size());
            }
            Platform.runLater(() -> {export();});
        }catch (Exception e){
            Platform.runLater(() -> {informationDialog("系统内部错误 "+ e.getMessage());});
        }finally {
            if(sqlSession!=null){
                sqlSession.close();
            }
            importBtn.setDisable(false);
            executeBtn.setDisable(false);
            progressIndicator.setVisible(false);
            table.setDisable(false);
        }
    }

    private void export(){
        try{
            FileChooser fileChooser = new FileChooser();
            FileSystemView fsv = FileSystemView.getFileSystemView();
            File homeDirectory = fsv.getHomeDirectory();
            fileChooser.setInitialDirectory(homeDirectory);
            fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(Main.stage);
            if(file != null){
                PrintWriter pw = new PrintWriter(file);
                ObservableList tableColumns = table.getColumns();
                String header = "NAME,CODE,ACCESS_URL,CSPID,原始,一次映射,二次映射";
                pw.println(header);
                for(Data data : tableList){
                    StringBuffer content = new StringBuffer();
                    content.append(checkNull(data.getName())).append(",")
                            .append(checkNull(data.getCode())).append(",")
                            .append(checkNull(data.getAccessUrl())).append(",")
                            .append(checkNull(data.getCspid())).append(",")
                            .append(checkNull(data.getOriginal())).append(",")
                            .append(checkNull(data.getOneMapping())).append(",")
                            .append(checkNull(data.getTwoMapping()));
                    pw.println(content);
                }
                pw.flush();
                pw.close();
            }
        }catch (Exception e){
            informationDialog("系统内部错误 "+ e.getMessage());
        }

    }

    private String checkNull(String src){
        if(StringUtils.isNotBlank(src)){
            return src;
        }else{
            return "";
        }
    }

    public void informationDialog(String header){
        Alert _alert = new Alert(Alert.AlertType.ERROR);
        _alert.setTitle("信息");
        _alert.setHeaderText(header);
       // _alert.setContentText(message);
        _alert.initOwner(Main.stage);
        _alert.show();
    }

}

