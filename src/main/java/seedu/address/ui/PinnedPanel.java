package seedu.address.ui;

import java.util.logging.Logger;

import org.fxmisc.easybind.EasyBind;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.commons.events.ui.PinPersonEvent;
import seedu.address.model.Model;
import seedu.address.model.person.ReadOnlyPerson;


/**
 * Controller for a help page
 */
public class PinnedPanel extends UiPart<Region> {
    private static final String FXML = "PinnedPanel.fxml";
    private static final Logger logger = LogsCenter.getLogger(PinnedPanel.class);

    @FXML
    private ListView<PersonCard> pinnedListView;
    private ObservableList<ReadOnlyPerson> personList;

    public PinnedPanel(ObservableList<ReadOnlyPerson> personList) {
        super(FXML);
        this.personList = personList;
        setConnections();
        registerAsAnEventHandler(this);
    }

    private void setConnections() {
        ObservableList<PersonCard> mappedList = EasyBind.map(
                personList.filtered(Model.PREDICATE_SHOW_ONLY_PINNED), (person) -> new PersonCard(person, personList.indexOf(person) + 1));
        pinnedListView.setItems(mappedList);
        pinnedListView.setCellFactory(listView -> new pinnedListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void setEventHandlerForSelectionChangeEvent() {
        pinnedListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        logger.fine("Selection in person list panel changed to : '" + newValue + "'");
                        raise(new PersonPanelSelectionChangedEvent(newValue));
                    }
                });
    }

    /**
     * Scrolls to the {@code PersonCard} at the {@code index} and selects it.
     */
    private void scrollTo(int index) {
        Platform.runLater(() -> {
            pinnedListView.scrollTo(index);
            pinnedListView.getSelectionModel().clearAndSelect(index);
        });
    }

    class pinnedListViewCell extends ListCell<PersonCard> {

        @Override
        protected void updateItem(PersonCard person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(person.getRoot());
            }
        }
    }

    @Subscribe
    private void handlePinPersonEvent(PinPersonEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        ObservableList<PersonCard> mappedList = EasyBind.map(
                personList.filtered(Model.PREDICATE_SHOW_ONLY_PINNED), (person) -> new PersonCard(person, personList.indexOf(person) + 1));
        pinnedListView.setItems(mappedList);
        pinnedListView.setCellFactory(listView -> new pinnedListViewCell());
    }
}