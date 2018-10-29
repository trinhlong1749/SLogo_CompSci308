package view.view_component;

import commands.CommandInitializer;
import commands.GenericCommand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import controller.Controller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.VariableMap;
import view.turtleView.TurtleDriver;
import view.turtleView.TurtleInfo;

import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * DropDownButtons
 *
 * @author brookekeene
 */
public class DropDownButtons extends VBox {
    public static final String RESOURCE_PACKAGE = "text/view";
    public static final String PATH_TO_LANGUAGES = "languages/";
    public static final String HELP_DOCUMENT = "commands.html";
    public static final String NEW_LINE = "\n";
    public static final String EQUALS = " = ";
    private ResourceBundle myResources;
    private TextFlow historyTab;
    private TextFlow variablesTab;
    private TextFlow userTab;
    private VBox paletteBox;
    private LogoScreen myDisplay;
    private Controller myController;
    private VariableMap myVarMap;
    private int dropdownWidth;
    private int dropdownHeight;

    /**
     * Constructors
     */
    public DropDownButtons(LogoScreen ls, Controller controller) {
        myDisplay = ls;
        myController = controller;
        myVarMap = myController.getVariableSupplier();
        myResources = ResourceBundle.getBundle(RESOURCE_PACKAGE);

        dropdownWidth = Integer.parseInt(myResources.getString("Dropdown_Width"));
        dropdownHeight = Integer.parseInt(myResources.getString("Dropdown_Height"));

        historyTab = new TextFlow();
        variablesTab = new TextFlow();
        userTab = new TextFlow();
        paletteBox = new VBox();

        historyTab.setMaxWidth(dropdownWidth);
        variablesTab.setMaxWidth(dropdownWidth);
        userTab.setMaxWidth(dropdownWidth);

        this.setId("dropdown-menu");
    }

    public void makeTabs() {
        this.getChildren().add(addControls());
        this.getChildren().add(addTurtleState());
        this.getChildren().add(addBackgroundTab());
        this.getChildren().add(addPenTab());
        this.getChildren().add(addTurtleTab());
        this.getChildren().add(addHistoryTab());
        this.getChildren().add(addVariablesTab());
        this.getChildren().add(addUserCommandTab());
        this.getChildren().add(addPaletteTab());
        this.getChildren().add(addLanguageTab());
        this.getChildren().add(addHelpTab());
    }

    /**
     * adds heading to the control panel dropdown menu
     * @return HBox containing Label for header
     */
    private HBox addControls() {
        HBox controlHeader = new HBox();
        controlHeader.setPadding(new Insets(Integer.parseInt(myResources.getString("Padding"))));
        controlHeader.setAlignment(Pos.CENTER_LEFT);
        Label header = new Label();
        header.setText(myResources.getString("Control"));
        controlHeader.getChildren().add(header);
        return controlHeader;
    }

    //TODO: update
    private TitledPane addTurtleState() {
        TitledPane turtleState = new TitledPane();
        VBox turtleStateBox = turtleState();
        turtleState.setText(myResources.getString("State"));
        turtleState.setContent(turtleStateBox);
        turtleState.setExpanded(false);
        return turtleState;
    }

    private VBox turtleState() {
        VBox turtleStateBox = new VBox();
        TurtleDriver myTurtles = myDisplay.getMyTurtle();
        turtleStateBox.getChildren().add(new TurtleInfo(myTurtles));
        return turtleStateBox;
    }

    public void editCurrentState() {

    }

    /**
     * adds background tab containing controls for background settings
     * @return TitledPane containing background controls
     */
    private TitledPane addBackgroundTab() {
        TitledPane backgroundTab = new TitledPane();
        VBox backBox = backgroundSettings();
        backgroundTab.setText(myResources.getString("Background"));
        backgroundTab.setContent(backBox);
        backgroundTab.setExpanded(false);
        return backgroundTab;
    }

    /**
     * creates a VBox containing the background controls (color)
     * @return VBox containing pre-defined ColorBox
     */
    private VBox backgroundSettings() {
        ColorBox colorChoices = new ColorBox();
        VBox backgroundControls = colorChoices.makeBox();
        colorChoices.setOnAction(e -> {
            String newBackColor = colorChoices.getColor();
            myDisplay.setBackGroundColor(Color.valueOf(newBackColor));
        });
        return backgroundControls;
    }

    //TODO: get current state from TurtleManager
    /**
     * adds pen tab containing controls for pen settings
     * @return TitledPane containing pen controls
     */
    private TitledPane addPenTab() {
        TitledPane penTab = new TitledPane();
        VBox penBox = penSettings();
        penTab.setText(myResources.getString("Pen"));
        penTab.setContent(penBox);
        penTab.setExpanded(false);
        return penTab;
    }

    /**
     * creates a VBox containing the pen controls (color)
     * @return VBox containing pre-defined ColorBox
     */
    private VBox penSettings() {
        ColorBox colorChoices = new ColorBox();
        VBox penControls = colorChoices.makeBox();
        colorChoices.setOnAction(e -> {
            String newPenColor = colorChoices.getColor();
            myDisplay.getMyTurtle().setMyPenColor(Color.valueOf(newPenColor));
        });
        return penControls;
    }

    //TODO: get current state from TurtleManager
    /**
     * adds turtle tab containing controls for turtle settings
     * @return TitledPane containing turtle controls
     */
    private TitledPane addTurtleTab() {
        TitledPane turtleSetting = new TitledPane();
        VBox turtleBox = turtleSettings();
        turtleSetting.setText(myResources.getString("Turtle"));
        turtleSetting.setContent(turtleBox);
        turtleSetting.setExpanded(false);
        return turtleSetting;
    }

    /**
     * creates a VBox containing the turtle controls (image)
     * @return VBox containing pre-defined ImageChooser
     */
    private VBox turtleSettings() {
        VBox turtleControls = new VBox();
        ImageChooser imageBox = new ImageChooser();
        Button chooserBtn = imageBox.getButton();
        chooserBtn.setOnAction(value -> {
            File file = imageBox.getFileChooser().showOpenDialog(getScene().getWindow());
            //TODO: error check
            if(file.toString().contains(".png") || file.toString().contains(".jpeg")) {
                imageBox.setFileName(file.toString());
                Image myImage = new Image(file.toURI().toString());
                myDisplay.getMyTurtle().getView().setImage(myImage);
            }
        });
        turtleControls.getChildren().add(imageBox);
        return turtleControls;
    }


    /**
     * adds history tab containing the user's input command history
     * @return TitledPane containing the command history
     */
    private TitledPane addHistoryTab() {
        TitledPane history = new TitledPane();
        VBox historyBox = displayHistory();
        history.setText(myResources.getString("History"));
        history.setContent(historyBox);
        history.setExpanded(false);
        return history;
    }

    /**
     *
     * @return
     */
    private VBox displayHistory() {
        VBox history = new VBox();
        ScrollPane scroller = new ScrollPane();
        scroller.setMaxWidth(dropdownWidth);
        scroller.setContent(historyTab);
        history.getChildren().add(scroller);
        return history;
    }

    /**
     *
     * @param command
     */
    public void editHistoryTab(String command) {
        Text text = new Text(command + NEW_LINE);
        historyTab.getChildren().add(text);
    }

    /**
     * adds variables tab containing the variables available to the user
     * @return TitledPane containing available variables
     */
    private TitledPane addVariablesTab() {
        TitledPane variables = new TitledPane();
        VBox varBox = displayVariable();
        variables.setText(myResources.getString("Variables"));
        variables.setContent(varBox);
        variables.setExpanded(false);
        return variables;
    }

    /**
     *
     * @return
     */
    private VBox displayVariable() {
        VBox variables = new VBox();
        ScrollPane scroller = new ScrollPane();
        scroller.setMaxWidth(dropdownWidth);
        scroller.setContent(variablesTab);
        variables.getChildren().add(scroller);
        return variables;
    }

    /**
     *
     */
    public void editVariableTab() {
        variablesTab.getChildren().clear();
        for(String key : myVarMap.getVariables().keySet()) {
            Text text = new Text(key + EQUALS + myVarMap.getVariable(key) + NEW_LINE);
            variablesTab.getChildren().add(text);
        }
    }

    /**
     * adds user-defined commands tab containing the user's pre-defined commands
     * @return TitledPane containing the user-defined commands
     */
    private TitledPane addUserCommandTab() {
        TitledPane userCommands = new TitledPane();
        VBox userBox = displayUserCommands();
        userCommands.setText(myResources.getString("UserCommands"));
        userCommands.setContent(userBox);
        userCommands.setExpanded(false);
        return userCommands;
    }

    /**
     *
     * @return
     */
    private VBox displayUserCommands() {
        VBox commands = new VBox();
        ScrollPane scroller = new ScrollPane();
        scroller.setMaxWidth(dropdownWidth);
        scroller.setContent(userTab);
        commands.getChildren().add(scroller);
        return commands;
    }

    /**
     *
     */
    public void editUserCommandTab() {
        userTab.getChildren().clear();
        CommandInitializer initializer = myController.getInitializerSupplier();
        Map<String, GenericCommand> userMap = initializer.getUserCommands();
        for(String key : userMap.keySet()) {
            Text text = new Text(key);
            userTab.getChildren().add(text);
        }
    }

    //TODO: display new colors in palette

    /**
     *
     * @return
     */
    private TitledPane addPaletteTab() {
        TitledPane paletteTab = new TitledPane();
        this.paletteDefault();
        paletteTab.setText(myResources.getString("Palette"));
        paletteTab.setContent(paletteBox);
        paletteTab.setExpanded(false);
        return paletteTab;
    }

    /**
     *
     */
    private void paletteDefault() {
        Palette myPalette = myVarMap.getPalette();
        Label colorLbl = new Label(myResources.getString("ColorDefault"));
        paletteBox.getChildren().add(colorLbl);
        for (int i: myPalette.keySet()) {
            Text temp = new Text(i + EQUALS + myPalette.getColorMap().get(i));
            paletteBox.getChildren().add(temp);
        }
    }

    /**
     *
     * @param index
     * @param r
     * @param g
     * @param b
     */
    public void editPalette(int index, int r, int g, int b) {
        Palette myPalette = myVarMap.getPalette();
        myPalette.addColor(index, r, g, b);
        Text temp = new Text(index + EQUALS + myPalette.getColorMap().get(index));
        paletteBox.getChildren().add(temp);
    }

    /**
     * adds languages tab containing controls for the languages understood for commands
     * @return TitledPane containing language controls
     */
    private TitledPane addLanguageTab() {
        TitledPane languageTab = new TitledPane();
        VBox languageBox = languageSettings();
        languageTab.setText(myResources.getString("Language"));
        languageTab.setContent(languageBox);
        languageTab.setExpanded(false);
        return languageTab;
    }

    /**
     * creates a VBox containing the language controls (command language)
     * @return VBox containing ChoiceBox
     */
    private VBox languageSettings() {
        VBox languageControls = new VBox();
        Label langChoice = new Label(myResources.getString("LanguageChoice"));
        LanguageBox languageCB = new LanguageBox();
        languageCB.makeBox();
        languageCB.setOnAction(e -> {
            String newLanguage = languageCB.getChoice();
            ResourceBundle newLangResources = ResourceBundle.getBundle(PATH_TO_LANGUAGES + newLanguage);
            myController.setLanguageConsumer(newLangResources);
        });
        languageControls.getChildren().addAll(langChoice, languageCB);
        return languageControls;
    }

    /**
     * adds help tab containing a link that directs the user to a command reference page
     * @return TitledPane containing reference link
     */
    private TitledPane addHelpTab() {
        TitledPane helpTab = new TitledPane();
        VBox helpBox = new VBox();
        Hyperlink helpLink = this.linkHelp();
        helpBox.getChildren().addAll(helpLink);
        helpTab.setText(myResources.getString("Help"));
        helpTab.setContent(helpBox);
        helpTab.setExpanded(false);
        return helpTab;
    }

    /**
     * creates Hyperlink to help documentation
     * @return
     */
    private Hyperlink linkHelp() {
        Hyperlink helpLink = new Hyperlink(myResources.getString("HelpBasic"));
        helpLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PopupWindow helpWindow = new PopupWindow(myResources.getString("HelpBasic"), HELP_DOCUMENT);
                helpWindow.display();
            }
        });
        return helpLink;
    }
}
