package seedu.address.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setMinWidth(0);
        personListView.setMaxWidth(Double.MAX_VALUE);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    static double getVisibleVerticalScrollBarWidth(ListView<?> listView) {
        ScrollBar verticalScrollBar = findVerticalScrollBar(listView);
        return verticalScrollBar != null && verticalScrollBar.isVisible() ? verticalScrollBar.getWidth() : 0;
    }

    private static ScrollBar findVerticalScrollBar(ListView<?> listView) {
        if (listView == null || listView.getSkin() == null) {
            return null;
        }

        Node verticalScrollBar = listView.lookup(".scroll-bar:vertical");
        if (verticalScrollBar instanceof ScrollBar scrollBar) {
            return scrollBar;
        }

        for (Node node : listView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                return scrollBar;
            }
        }

        return null;
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        private PersonCard personCard;

        PersonListViewCell() {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setMinWidth(0);
            setMaxWidth(Control.USE_PREF_SIZE);
        }

        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (getGraphic() instanceof Region currentCard) {
                currentCard.prefWidthProperty().unbind();
            }

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                if (personCard == null) {
                    personCard = new PersonCard(person, getIndex() + 1);
                } else {
                    personCard.update(person, getIndex() + 1);
                }

                Region cardRoot = personCard.getRoot();
                cardRoot.setMinWidth(0);
                cardRoot.setMaxWidth(Double.MAX_VALUE);
                cardRoot.prefWidthProperty().bind(Bindings.createDoubleBinding(
                    this::computeCardWidth,
                        getListView().widthProperty(),
                        getListView().heightProperty(),
                        getListView().layoutBoundsProperty(),
                        getListView().needsLayoutProperty(),
                        getListView().sceneProperty(),
                        getListView().skinProperty()));
                setGraphic(cardRoot);
                setText(null);
            }
        }

        private double computeCardWidth() {
            return Math.max(0, getListView().getWidth() - getVisibleVerticalScrollBarWidth(getListView()));
        }

        @Override
        protected double computePrefHeight(double width) {
            if (getGraphic() instanceof Region cardRoot) {
                double contentWidth;
                if (cardRoot.getWidth() > 0) {
                    contentWidth = Math.max(0, cardRoot.getWidth());
                } else if (width < 0) {
                    contentWidth = Math.max(0, cardRoot.prefWidth(-1));
                } else {
                    double availableWidth = width - snappedLeftInset() - snappedRightInset()
                            - getVisibleVerticalScrollBarWidth(getListView());
                    contentWidth = Math.max(0, availableWidth);
                }
                return cardRoot.prefHeight(contentWidth) + snappedTopInset() + snappedBottomInset();
            }
            return super.computePrefHeight(width);
        }
    }

}
