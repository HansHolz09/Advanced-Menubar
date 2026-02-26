package dev.hansholz.advancedmenubar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

sealed interface MenuIcon {
    data class SFSymbol(val name: String, val template: Boolean = true) : MenuIcon
    data class Png(val bytes: ByteArray, val template: Boolean = true) : MenuIcon {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Png

            if (template != other.template) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = template.hashCode()
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    data class File(val path: String, val template: Boolean = true) : MenuIcon
}

@Composable
fun rememberMenuIconFrom(imageVector: ImageVector, template: Boolean = true): MenuIcon {
    val density = LocalDensity.current
    val px = with(density) { (16.dp * density.density).roundToPx().coerceAtLeast(1) }

    val painter = rememberVectorPainter(imageVector)
    val bytes = remember(imageVector, px, density) {
        val ib = ImageBitmap(px, px)
        val canvas = Canvas(ib)
        val drawScope = CanvasDrawScope()

        drawScope.draw(
            density = Density(density.density, density.fontScale),
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = Size(px.toFloat(), px.toFloat())
        ) {
            with(painter) { draw(Size(px.toFloat(), px.toFloat())) }
        }

        val awt = ib.toAwtImage()
        val baos = ByteArrayOutputStream()
        ImageIO.write(awt, "png", baos)
        baos.toByteArray()
    }

    return MenuIcon.Png(bytes, template)
}