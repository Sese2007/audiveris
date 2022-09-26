//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                O c t a v e S h i f t S y m b o l                               //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//
//  Copyright © Audiveris 2022. All rights reserved.
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the
//  GNU Affero General Public License as published by the Free Software Foundation, either version
//  3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this
//  program.  If not, see <http://www.gnu.org/licenses/>.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package org.audiveris.omr.ui.symbol;

import org.audiveris.omr.constant.Constant;
import org.audiveris.omr.constant.ConstantSet;
import org.audiveris.omr.glyph.Shape;
import org.audiveris.omr.sheet.Scale;
import org.audiveris.omr.sheet.Scale.Fraction;
import org.audiveris.omr.sig.inter.OctaveShiftInter;
import org.audiveris.omr.sig.inter.OctaveShiftInter.Kind;
import static org.audiveris.omr.ui.symbol.Alignment.TOP_LEFT;
import static org.audiveris.omr.ui.symbol.ShapeSymbol.decoComposite;

import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Class <code>OctaveShiftSymbol</code> defines a symbol for on octave shift, composed of
 * a number value (8/15/22) and a "decoration" made of a horizontal dashed line and
 * perhaps a final hook.
 * <p>
 * Note the final hook is not drawn in this symbol but is kept to build a suitable model on demand.
 *
 * @see OctaveShiftInter
 *
 * @author Hervé Bitteur
 */
public class OctaveShiftSymbol
        extends DecorableSymbol
{
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final Constants constants = new Constants();

    /** Default thickness of a ottava line (in pixels). */
    public static final double DEFAULT_THICKNESS = constants.defaultThickness.getValue();

    /** Default length of an ottava line (in interline fraction). */
    public static final Fraction DEFAULT_LINE_LENGTH = constants.defaultLineLength;

    /** Default length of a hook leg (in interline fraction). */
    public static final Fraction DEFAULT_HOOK_LENGTH = constants.defaultHookLength;

    /** Default stroke to paint line and hook. */
    public static Stroke DEFAULT_STROKE = new BasicStroke((float) DEFAULT_THICKNESS,
                                                          BasicStroke.CAP_ROUND,
                                                          BasicStroke.JOIN_ROUND,
                                                          10.0f,
                                                          new float[]{4.0f, 4.0f},
                                                          0.0f);

    //~ Instance fields ----------------------------------------------------------------------------
    // ALTA or BASSA
    public Kind kind;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Create a standard size OctaveShiftSymbol, ALTA by default
     *
     * @param shape OTTAVA, QUINDICESIMA or VENTIDUESIMA
     * @param codes precise code for 8/15/22 value part
     */
    public OctaveShiftSymbol (Shape shape,
                              int... codes)
    {
        this(shape, Kind.ALTA, codes);
    }

    /**
     * Create a standard size OctaveShiftSymbol.
     *
     * @param shape OTTAVA, QUINDICESIMA or VENTIDUESIMA
     * @param kind  ALTA or BASSA
     * @param codes precise code for 8/15/22 value part
     */
    public OctaveShiftSymbol (Shape shape,
                              Kind kind,
                              int... codes)
    {
        super(shape, codes);
        this.kind = kind;
    }

    //~ Methods ------------------------------------------------------------------------------------
    //----------//
    // getModel //
    //----------//
    @Override
    public OctaveShiftInter.Model getModel (MusicFont font,
                                            Point location)
    {
        final MyParams p = getParams(font);
        p.model.translate(p.vectorTo(location));

        return p.model;
    }

    //-----------//
    // getParams //
    //-----------//
    @Override
    protected MyParams getParams (MusicFont font)
    {
        final MyParams p = new MyParams();

        // 8/15/22 value layout
        p.layout = font.layout(getString());

        final Rectangle2D rs = p.layout.getBounds(); // Just value bounds
        final double w = rs.getWidth();
        final double h = rs.getHeight();
        final Rectangle2D valueBox = new Rectangle2D.Double(0, 0, w, h);
        final Point2D valueCenter = new Point2D.Double(w / 2, h / 2);
        p.rect = valueBox;

        if (isDecorated) {
            final double width = font.getStaffInterline() * constants.defaultLineLength.getValue();
            final double height = font.getStaffInterline() * constants.defaultHookLength.getValue();

            final Point2D lineRight = new Point2D.Double(w + width, h / 2);

            final int hookDir = (kind == Kind.ALTA) ? 1 : -1;
            final Point2D hookEnd = new Point2D.Double(w + width, h / 2 + hookDir * height);
            p.model = new OctaveShiftInter.Model(shape, kind, valueCenter, lineRight, hookEnd);

            p.rect.add(lineRight);

            // Focus is on value center
            p.offset = new Point2D.Double(-width / 2, 0);
        }

        return p;
    }

    //-------//
    // paint //
    //-------//
    @Override
    protected void paint (Graphics2D g,
                          Params params,
                          Point2D location,
                          Alignment alignment)
    {
        final MyParams p = (MyParams) params;
        Point2D loc = alignment.translatedPoint(TOP_LEFT, p.rect, location);

        // 8 or 15 or 22
        MusicFont.paint(g, p.layout, loc, TOP_LEFT);
        final Rectangle2D symBounds = p.layout.getBounds();

        if (isDecorated) {
            p.model.translate(loc.getX(), loc.getY());
            final Composite oldComposite = g.getComposite();
            g.setComposite(decoComposite);
            // Line (drawn from right to left, to preserve the right end)
            final Stroke oldStroke = g.getStroke();
            g.setStroke(DEFAULT_STROKE);
            g.draw(new Line2D.Double(
                    p.model.lineRight,
                    new Point2D.Double(p.model.valueCenter.getX() + symBounds.getWidth() / 2,
                                       p.model.valueCenter.getY())));

            // No hook drawn in symbol
            ///g.draw(new Line2D.Double(p.model.lineRight, p.model.hookEnd));
            //
            g.setComposite(oldComposite);
            g.setStroke(oldStroke);
        }
    }

    //~ Inner Classes ------------------------------------------------------------------------------
    //-----------//
    // Constants //
    //-----------//
    private static class Constants
            extends ConstantSet
    {

        private final Constant.Double defaultThickness = new Constant.Double(
                "pixels",
                2.0,
                "Default ottava line thickness");

        private final Scale.Fraction defaultLineLength = new Scale.Fraction(
                3.0,
                "Default length for octave shift line");

        private final Scale.Fraction defaultHookLength = new Scale.Fraction(
                1.5,
                "Default length for ending hook");
    }

    //--------//
    // Params //
    //--------//
    protected static class MyParams
            extends BasicSymbol.Params
    {

        // offset: used
        // layout: used for value symbol
        // rect:   global image (value + line only)
        //
        // model
        OctaveShiftInter.Model model;
    }
}
