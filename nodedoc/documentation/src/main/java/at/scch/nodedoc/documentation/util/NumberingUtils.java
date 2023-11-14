package at.scch.nodedoc.documentation.util;

import at.scch.nodedoc.documentation.displaymodel.DisplayMethod;
import at.scch.nodedoc.documentation.displaymodel.DisplaySection;
import at.scch.nodedoc.documentation.displaymodel.DisplayType;

import java.util.ArrayList;
import java.util.List;

public class NumberingUtils {

    public static void calculateHeadingNumbers(List<? extends DisplaySection> chapters, int firstChapterNumber) {
        int chapterNumber = firstChapterNumber;
        for (var chapter : chapters) {
            chapter.getHeadingNumber().setHeadingNumberComponents(List.of(chapterNumber++));
            calculateHeadingNumbersForSubsection(chapter);
        }
    }

    private static void calculateHeadingNumbersForSubsection(DisplaySection section) {
        int sectionNumber = 1;
        for (var subSection : section.getSubSections()) {
            var headingNumberComponents = new ArrayList<>(section.getHeadingNumber().getHeadingNumberComponents());
            headingNumberComponents.add(sectionNumber++);
            subSection.getHeadingNumber().setHeadingNumberComponents(headingNumberComponents);
            calculateHeadingNumbersForSubsection(subSection);
        }
    }

    public static void calculateFigureNumbers(List<? extends DisplaySection> chapters) {
        calculateFigureNumbers(chapters, 1);
    }

    private static int calculateFigureNumbers(List<? extends DisplaySection> sections, int nextNumber) {
        for (var subSection : sections) {
            if (subSection instanceof DisplayType) {
                ((DisplayType) subSection).getGraphFigure().setFigureNumber(nextNumber++);
            }
            nextNumber = calculateFigureNumbers(subSection.getSubSections(), nextNumber);
        }
        return nextNumber;
    }

    public static void calculateTableNumbers(List<? extends DisplaySection> chapters) {
        calculateTableNumbers(chapters, 1);
    }

    private static int calculateTableNumbers(List<? extends DisplaySection> sections, int nextNumber) {
        for (var subSection : sections) {
            if (subSection instanceof DisplayType) {
                ((DisplayType) subSection).getDefinitionTable().setTableNumber(nextNumber++);
            }
            if (subSection instanceof DisplayMethod) {
                ((DisplayMethod) subSection).getMethodArgumentsTable().setTableNumber(nextNumber++);
            }
            nextNumber = calculateTableNumbers(subSection.getSubSections(), nextNumber);
        }
        return nextNumber;
    }

}
