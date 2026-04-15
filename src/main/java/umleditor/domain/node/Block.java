package umleditor.domain.node;

import umleditor.domain.BaseElement;

import java.util.List;

public abstract class Block extends BaseElement {
	public abstract List<String> collectOwnedNodeIds();
}



