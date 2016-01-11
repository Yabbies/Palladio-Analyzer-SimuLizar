package org.palladiosimulator.simulizar.runtimestate;

import java.util.Collections;
import java.util.List;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;

public class FQComponentID {

    private final List<AssemblyContext> assembyContextPath;

    public FQComponentID(final List<AssemblyContext> assemblyContextPath) {
        super();
        this.assembyContextPath = Collections.unmodifiableList(assemblyContextPath);
    }

    /**
     * @return the assembyContextPath
     */
    public final List<AssemblyContext> getAssembyContextPath() {
        return this.assembyContextPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (final AssemblyContext ctx : this.assembyContextPath) {
            result = prime * result + ctx.getId().hashCode();
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final FQComponentID other = (FQComponentID) obj;
        if (this.assembyContextPath == null) {
            if (other.assembyContextPath != null) {
                return false;
            }
        } else {
            if (this.assembyContextPath.size() != other.assembyContextPath.size()) {
                return false;
            }
            for (int i = 0; i < this.assembyContextPath.size(); i++) {
                if (!this.assembyContextPath.get(i).getId().equals(other.assembyContextPath.get(i).getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder pathBuilder = new StringBuilder();
        for (final AssemblyContext assembyContext : this.assembyContextPath) {
            pathBuilder.append(assembyContext.getEntityName());
            pathBuilder.append("::");
        }
        pathBuilder.delete(pathBuilder.length() - 2, pathBuilder.length());
        return "FQComponentID [assembyContextPath=" + pathBuilder.toString() + "]";
    }

}
