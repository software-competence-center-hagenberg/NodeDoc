package at.scch.nodedoc.documentation.displaymodel;

import at.scch.nodedoc.documentation.displaymodel.graph.GraphDisplayData;
import at.scch.nodedoc.documentation.template.GraphFigureTemplate;
import com.fizzed.rocker.RockerContent;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class GraphFigure implements Renderable {

    @Setter
    private int figureNumber;

    @Setter
    private GraphDisplayData graphData;

    @Override
    public DefaultRockerModel structureTemplate(RockerContent content) {
        return GraphFigureTemplate.template(this);
    }

    public abstract Renderable getCaption();
}
