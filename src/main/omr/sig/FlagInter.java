//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                        F l a g I n t e r                                       //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//  Copyright © Herve Bitteur and others 2000-2014. All rights reserved.
//  This software is released under the GNU General Public License.
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package omr.sig;

import omr.glyph.Shape;
import omr.glyph.facets.Glyph;

/**
 * Class {@code FlagInter} represents one or several flags.
 *
 * @author Hervé Bitteur
 */
public class FlagInter
        extends AbstractInter
{
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new FlagInter object.
     *
     * @param glyph underlying glyph
     * @param shape precise shape
     * @param grade evaluation value
     */
    public FlagInter (Glyph glyph,
                      Shape shape,
                      double grade)
    {
        super(glyph, null, shape, grade);
    }

    //~ Methods ------------------------------------------------------------------------------------
    //--------//
    // accept //
    //--------//
    @Override
    public void accept (InterVisitor visitor)
    {
        visitor.visit(this);
    }
}