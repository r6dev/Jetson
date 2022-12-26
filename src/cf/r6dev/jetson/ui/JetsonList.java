package cf.r6dev.jetson.ui;

import cf.r6dev.jetson.utils.JetRL;

import javax.swing.*;
import java.awt.*;

public class JetsonList extends JScrollPane {
    private final JPanel listComponent;

    public JetsonList(JPanel list, int verticalPolicy) {
        super(list, verticalPolicy, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listComponent = list;

        list.setBackground(JetRL.TITLE_BAR_COLOR);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        setBorder(null);
        setPreferredSize(new Dimension(180, 128));
        setComponentZOrder(getVerticalScrollBar(), 0);
        setComponentZOrder(getViewport(), 1);
        getVerticalScrollBar().setUnitIncrement(9);
        getVerticalScrollBar().setOpaque(false);
        setLayout(JetsonScrollBarUI.newLayout());
        getVerticalScrollBar().setUI(new JetsonScrollBarUI());
    }

    public JetsonList(JPanel list) {
        this(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    @SuppressWarnings("unused") public JPanel getList() {
        return listComponent;
    }
}
