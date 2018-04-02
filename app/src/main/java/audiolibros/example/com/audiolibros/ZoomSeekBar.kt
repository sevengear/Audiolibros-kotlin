package audiolibros.example.com.audiolibros

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Miguel Á. Núñez on 15/02/2018.
 */

class ZoomSeekBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Valor a controlar
    private var valor = 160 // valor seleccionado
    var valMin = 100
        set(valMin) {
            if (valMin <= valor) {
                field = valMin
            }
        } // valor mínimo
    var valMax = 200
        set(valMax) {
            if (valMax >= valor) {
                field = valMax
            }
        } // valor máximo
    private var escalaMin = 150 // valor mínimo visualizado
    private var escalaMax = 180 // valor máximo visualizado
    var escalaIni = 100
        set(escalaIni) {
            if (escalaMin <= escalaIni && escalaIni <= escalaMax) {
                field = escalaIni
            }
        } // origen de la escala
    var escalaRaya = 2
        set(escalaRaya) {
            if (escalaRaya <= this.escalaRayaLarga) {
                field = escalaRaya
            }
        } // cada cuantas unidades una rayas
    var escalaRayaLarga = 5
        set(escalaRayaLarga) {
            if (this.escalaRaya <= escalaRayaLarga) {
                field = escalaRayaLarga
            }
        } // cada cuantas rayas una larga
    // Dimensiones en pixels
    private var altoNumeros: Int = 0
    private var altoRegla: Int = 0
    private var altoBar: Int = 0
    private var altoPalanca: Int = 0
    private var anchoPalanca: Int = 0
    private var altoGuia: Int = 0
    // Valores que indican donde dibujar
    private var xIni: Int = 0
    private var yIni: Int = 0
    private var ancho: Int = 0
    // Objetos Rect con diferentes regiones
    private val escalaRect = Rect()
    private val barRect = Rect()
    private val guiaRect = Rect()
    private val palancaRect = Rect()
    // Objetos Paint globales para no tener que crearlos cada vez
    private val textoPaint = Paint()
    private val reglaPaint = Paint()
    private val guiaPaint = Paint()
    private val palancaPaint = Paint()
    internal var estado = Estado.SIN_PULSACION
    internal var antVal_0: Int = 0
    internal var antVal_1: Int = 0

    init {
        val dp = resources.displayMetrics.density
        val a = context.theme.obtainStyledAttributes(attrs,
                R.styleable.ZoomSeekBar, 0, 0)
        try {
            altoNumeros = a.getDimensionPixelSize(
                    R.styleable.ZoomSeekBar_altoNumeros, (30 * dp).toInt())
            altoRegla = a.getDimensionPixelSize(
                    R.styleable.ZoomSeekBar_altoRegla, (20 * dp).toInt())
            altoBar = a.getDimensionPixelSize(
                    R.styleable.ZoomSeekBar_altoBar, (70 * dp).toInt())
            altoPalanca = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoPalanca, (40 * dp).toInt())
            altoGuia = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoGuia, (10 * dp).toInt())
            anchoPalanca = a.getDimensionPixelSize(R.styleable.ZoomSeekBar_anchoPalanca, (20 * dp).toInt())
            textoPaint.textSize = a.getDimension(R.styleable.ZoomSeekBar_altoTexto, 16 * dp)
            textoPaint.color = a.getColor(R.styleable.ZoomSeekBar_colorTexto, Color.BLACK)
            reglaPaint.color = a.getColor(R.styleable.ZoomSeekBar_colorRegla, Color.BLACK)
            guiaPaint.color = a.getColor(R.styleable.ZoomSeekBar_colorGuia, Color.BLUE)
            palancaPaint.color = a.getColor(
                    R.styleable.ZoomSeekBar_colorPalanca, -0xffff81)
        } finally {
            a.recycle()
        }
        textoPaint.isAntiAlias = true
        textoPaint.textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        xIni = paddingLeft
        yIni = paddingTop
        ancho = width - paddingRight - paddingLeft
        barRect.set(xIni, yIni, xIni + ancho, yIni + altoBar)
        escalaRect.set(xIni, yIni + altoBar, xIni + ancho, yIni + altoBar
                + altoNumeros + altoRegla)
        val y = yIni + (altoBar - altoGuia) / 2
        guiaRect.set(xIni, y, xIni + ancho, y + altoGuia)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Dibujamos Barra con palanca
        canvas.drawRect(guiaRect, guiaPaint)
        var y = yIni + (altoBar - altoPalanca) / 2
        var x = xIni + ancho * (valor - escalaMin) / (escalaMax - escalaMin) - anchoPalanca / 2
        palancaRect.set(x, y, x + anchoPalanca, y + altoPalanca)
        canvas.drawRect(palancaRect, palancaPaint)
        palancaRect.set(x - anchoPalanca / 2, y, x + 3 * anchoPalanca / 2, y + altoPalanca)
        // Dibujamos Escala
        var v = this.escalaIni
        while (v <= escalaMax) {
            if (v >= escalaMin) {
                x = xIni + ancho * (v - escalaMin) / (escalaMax - escalaMin)
                if ((v - this.escalaIni) / this.escalaRaya % this.escalaRayaLarga == 0) {
                    y = yIni + altoBar + altoRegla
                    canvas.drawText(Integer.toString(v), x.toFloat(), (y + altoNumeros).toFloat(),
                            textoPaint)
                } else {
                    y = yIni + altoBar + altoRegla * 1 / 3
                }
                canvas.drawLine(x.toFloat(), (yIni + altoBar).toFloat(), x.toFloat(), y.toFloat(), reglaPaint)
            }
            v += this.escalaRaya
        }
    }

    // Variables globales usadas en onTouchEvent()
    internal enum class Estado {
        SIN_PULSACION, PALANCA_PULSADA, ESCALA_PULSADA, ESCALA_PULSADA_DOBLE
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x_0: Int
        val y_0: Int
        val x_1: Int
        val y_1: Int
        x_0 = event.getX(0).toInt()
        y_0 = event.getY(0).toInt()
        val val_0 = escalaMin + (x_0 - xIni) * (escalaMax - escalaMin) / ancho
        if (event.pointerCount > 1) {
            x_1 = event.getX(1).toInt()
            y_1 = event.getY(1).toInt()
        } else {
            x_1 = x_0
            y_1 = y_0
        }
        val val_1 = escalaMin + (x_1 - xIni) * (escalaMax - escalaMin) / ancho
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> if (palancaRect.contains(x_0, y_0)) {
                estado = Estado.PALANCA_PULSADA
            } else if (barRect.contains(x_0, y_0)) {
                if (val_0 > valor)
                    valor++
                else
                    valor--
                invalidate(barRect)
            } else if (escalaRect.contains(x_0, y_0)) {
                estado = Estado.ESCALA_PULSADA
                antVal_0 = val_0
            }
            MotionEvent.ACTION_POINTER_DOWN -> if (estado == Estado.ESCALA_PULSADA) {
                if (escalaRect.contains(x_1, y_1)) {
                    antVal_1 = val_1
                    estado = Estado.ESCALA_PULSADA_DOBLE
                }
            }
            MotionEvent.ACTION_UP -> estado = Estado.SIN_PULSACION
            MotionEvent.ACTION_POINTER_UP -> if (estado == Estado.ESCALA_PULSADA_DOBLE) {
                estado = Estado.ESCALA_PULSADA
            }
            MotionEvent.ACTION_MOVE -> {
                if (estado == Estado.PALANCA_PULSADA) {
                    valor = ponDentroRango(val_0, escalaMin, escalaMax)
                    invalidate(barRect)
                }
                if (estado == Estado.ESCALA_PULSADA_DOBLE) {
                    escalaMin = antVal_0 + (xIni - x_0) * (antVal_0 - antVal_1) / (x_0 - x_1)
                    escalaMin = ponDentroRango(escalaMin, this.valMin, valor)
                    escalaMax = antVal_0 + (ancho + xIni - x_0) * (antVal_0 - antVal_1) / (x_0 - x_1)
                    escalaMax = ponDentroRango(escalaMax, valor, this.valMax)
                    invalidate()
                }
            }
        }
        return true
    }

    internal fun ponDentroRango(valor: Int, valMin: Int, valMax: Int): Int {
        return if (valor < valMin) {
            valMin
        } else if (valor > valMax) {
            valMax
        } else {
            valor
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val altoDeseado = (altoNumeros + altoRegla + altoBar
                + paddingBottom + paddingTop)
        val alto = obtenDimension(heightMeasureSpec, altoDeseado)
        val anchoDeseado = 2 * altoDeseado
        val ancho = obtenDimension(widthMeasureSpec, anchoDeseado)
        setMeasuredDimension(ancho, alto)
    }

    private fun obtenDimension(measureSpec: Int, deseado: Int): Int {
        val dimension = View.MeasureSpec.getSize(measureSpec)
        val modo = View.MeasureSpec.getMode(measureSpec)
        return if (modo == View.MeasureSpec.EXACTLY) {
            dimension
        } else if (modo == View.MeasureSpec.AT_MOST) {
            Math.min(dimension, deseado)
        } else {
            deseado
        }
    }

    fun getVal(): Int {
        return valor
    }

    fun setVal(valor: Int) {
        if (this.valMin <= valor && valor <= this.valMax) {
            this.valor = valor
            escalaMin = Math.min(escalaMin, valor)
            escalaMax = Math.max(escalaMax, valor)
            invalidate()
        }
    }

    fun getEscalaMin(): Int {
        return escalaMin
    }

    fun setEscalaMin(escalaMin: Int) {
        if (escalaMin <= escalaMax) {
            this.escalaMin = escalaMin
        }
    }

    fun getEscalaMax(): Int {
        return escalaMax
    }

    fun setEscalaMax(escalaMax: Int) {
        if (escalaMax >= escalaMin) {
            this.escalaMax = escalaMax
        }
    }
}

