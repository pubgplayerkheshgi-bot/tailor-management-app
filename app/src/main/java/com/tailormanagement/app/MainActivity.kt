package com.tailormanagement.app

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

data class Measurements(
    var length: String = "40",
    var width: String = "18",
    var teera: String = "16",
    var sleeve: String = "24",
    var cutBen: String = "15",
    var cuff: String = "9",
    var shalwar: String = "40",
    var paincha: String = "8",
    var pocket: String = "6",
    var daman: String = "دامن چورس",
    var cutBenOption: String = "کٹ بین"
)

data class Customer(
    var id: Long,
    var name: String,
    var phone: String,
    var address: String = "",
    var notes: String = "",
    var measurements: Measurements = Measurements()
)

class MainActivity : AppCompatActivity() {

    private val navy = Color.rgb(6, 44, 76)
    private val navyDark = Color.rgb(3, 27, 48)
    private val card = Color.rgb(248, 251, 255)
    private val row = Color.rgb(243, 248, 253)
    private val stroke = Color.rgb(217, 233, 248)
    private val ink = Color.rgb(6, 44, 76)
    private val muted = Color.rgb(93, 112, 130)
    private val white = Color.WHITE

    private lateinit var store: Store
    private var customers = mutableListOf<Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = navyDark
        window.navigationBarColor = navyDark
        store = Store(this)
        customers = store.load()
        if (customers.isEmpty()) {
            customers.add(Customer(1, "محمد علی", "0301-1234567"))
            store.save(customers)
        }
        showCustomers()
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun base(): LinearLayout {
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(navyDark)
        root.layoutDirection = View.LAYOUT_DIRECTION_LTR
        return root
    }

    private fun header(title: String, back: Boolean = false, expand: Boolean = false): LinearLayout {
        val h = LinearLayout(this)
        h.orientation = LinearLayout.HORIZONTAL
        h.gravity = Gravity.CENTER_VERTICAL
        h.setPadding(dp(18), dp(8), dp(18), dp(8))
        h.setBackgroundColor(navy)
        val lp = LinearLayout.LayoutParams(-1, dp(64))
        h.layoutParams = lp

        if (back) {
            val b = TextView(this)
            b.text = "‹"
            b.textColor = white
            b.textSize = 42f
            b.gravity = Gravity.CENTER
            b.setPadding(0, 0, dp(8), 0)
            b.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            h.addView(b, LinearLayout.LayoutParams(dp(42), -1))
        }

        val t = TextView(this)
        t.text = title
        t.textColor = white
        t.textSize = 22f
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        t.gravity = Gravity.CENTER_VERTICAL
        h.addView(t, LinearLayout.LayoutParams(0, -1, 1f))

        if (expand) {
            val e = TextView(this)
            e.text = "⛶"
            e.textColor = white
            e.textSize = 27f
            e.gravity = Gravity.CENTER
            h.addView(e, LinearLayout.LayoutParams(dp(40), -1))
        }
        return h
    }

    private fun profileHeader(c: Customer): LinearLayout {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.HORIZONTAL
        box.gravity = Gravity.CENTER_VERTICAL
        box.setPadding(dp(18), dp(18), dp(18), dp(18))
        box.setBackgroundColor(white)

        val image = ImageView(this)
        image.setImageResource(com.tailormanagement.app.R.drawable.profile)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        box.addView(image, LinearLayout.LayoutParams(dp(112), dp(112)))

        val info = LinearLayout(this)
        info.orientation = LinearLayout.VERTICAL
        info.gravity = Gravity.CENTER_VERTICAL
        info.setPadding(dp(18), 0, 0, 0)

        val name = TextView(this)
        name.text = c.name
        name.textColor = ink
        name.textSize = 31f
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        name.textDirection = View.TEXT_DIRECTION_RTL
        info.addView(name)

        val phone = TextView(this)
        phone.text = "☎  ${c.phone}"
        phone.textColor = ink
        phone.textSize = 20f
        phone.setPadding(0, dp(4), 0, 0)
        info.addView(phone)

        box.addView(info, LinearLayout.LayoutParams(0, -1, 1f))
        return box
    }

    private fun cardRow(value: String, label: String, dropdown: Boolean = false): LinearLayout {
        val r = LinearLayout(this)
        r.orientation = LinearLayout.HORIZONTAL
        r.gravity = Gravity.CENTER_VERTICAL
        r.setPadding(dp(10), 0, dp(14), 0)
        r.background = rounded(row, stroke, 2, 14)
        r.layoutDirection = View.LAYOUT_DIRECTION_LTR

        val v = TextView(this)
        v.text = value
        v.textColor = ink
        v.textSize = 28f
        v.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        v.gravity = Gravity.CENTER
        v.background = rounded(Color.rgb(229, 240, 251), Color.TRANSPARENT, 0, 16)
        r.addView(v, LinearLayout.LayoutParams(dp(92), dp(58)))

        val l = TextView(this)
        l.text = label
        l.textColor = ink
        l.textSize = 25f
        l.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        l.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        l.textDirection = View.TEXT_DIRECTION_RTL
        l.setPadding(dp(12), 0, dp(4), 0)
        r.addView(l, LinearLayout.LayoutParams(0, -1, 1f))

        if (dropdown) {
            val d = TextView(this)
            d.text = "⌄"
            d.textColor = ink
            d.textSize = 26f
            d.gravity = Gravity.CENTER
            r.addView(d, LinearLayout.LayoutParams(dp(38), -1))
        }
        return r
    }

    private fun cuttingRows(c: Customer): LinearLayout {
        val m = c.measurements
        val list = LinearLayout(this)
        list.orientation = LinearLayout.VERTICAL
        list.setPadding(dp(26), dp(92), dp(26), dp(24))
        list.setBackgroundColor(white)

        val data = listOf(
            Pair("40", "لمبائی"),
            Pair(m.width, "چوڑائی"),
            Pair(m.teera, "تیرہ"),
            Pair(m.sleeve, "بازو"),
            Pair(m.cutBen, m.cutBenOption),
            Pair(m.cuff, "کف"),
            Pair(m.shalwar, "شلوار"),
            Pair(m.paincha, "پینسہ"),
            Pair(m.pocket, "پاکٹ")
        )

        data.forEach { item ->
            val r = cardRow(item.first, item.second)
            list.addView(r, LinearLayout.LayoutParams(-1, dp(78)).apply {
                bottomMargin = dp(8)
            })
        }

        // The final row is a two-option Daman choice. The selected option is shown,
        // with no extra numbering, matching the supplied cutting-screen reference.
        val daman = cardRow("", m.daman, false)
        list.addView(daman, LinearLayout.LayoutParams(-1, dp(78)))
        return list
    }

    private fun showCutting(c: Customer) {
        val root = base()
        root.addView(header("Cutting View", true, true))
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(white)
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.addView(profileHeader(c))
        content.addView(cuttingRows(c))
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun showCustomers() {
        val root = base()
        val h = header("Customers")
        val add = TextView(this)
        add.text = "+"
        add.textColor = white
        add.textSize = 32f
        add.gravity = Gravity.CENTER
        add.setOnClickListener { showCustomerEditor(null) }
        h.addView(add, LinearLayout.LayoutParams(dp(48), -1))
        root.addView(h)

        val list = LinearLayout(this)
        list.orientation = LinearLayout.VERTICAL
        list.setPadding(dp(12), dp(12), dp(12), dp(12))
        list.setBackgroundColor(card)

        customers.forEach { c ->
            val item = LinearLayout(this)
            item.orientation = LinearLayout.HORIZONTAL
            item.gravity = Gravity.CENTER_VERTICAL
            item.setPadding(dp(10), dp(8), dp(10), dp(8))
            item.background = rounded(white, stroke, 1, 14)

            val iv = ImageView(this)
            iv.setImageResource(R.drawable.profile)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            item.addView(iv, LinearLayout.LayoutParams(dp(60), dp(60)))

            val txt = LinearLayout(this)
            txt.orientation = LinearLayout.VERTICAL
            txt.setPadding(dp(14), 0, dp(8), 0)
            val nm = TextView(this)
            nm.text = c.name
            nm.textSize = 21f
            nm.textColor = ink
            nm.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            nm.textDirection = View.TEXT_DIRECTION_RTL
            val ph = TextView(this)
            ph.text = c.phone
            ph.textSize = 15f
            ph.textColor = muted
            txt.addView(nm)
            txt.addView(ph)
            item.addView(txt, LinearLayout.LayoutParams(0, -1, 1f))

            item.setOnClickListener { showCustomer(c) }
            list.addView(item, LinearLayout.LayoutParams(-1, dp(78)).apply { bottomMargin = dp(10) })
        }

        val scroll = ScrollView(this)
        scroll.addView(list)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun showCustomer(c: Customer) {
        val root = base()
        root.addView(header("Customer Profile", true))
        val scroll = ScrollView(this)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setBackgroundColor(card)
        box.addView(profileHeader(c))

        val measurements = button("Measurements")
        measurements.setOnClickListener { showEditor(c) }
        box.addView(measurements, LinearLayout.LayoutParams(-1, dp(58)).apply {
            setMargins(dp(18), dp(18), dp(18), dp(8))
        })

        val cutting = button("Cutting View")
        cutting.setOnClickListener { showCutting(c) }
        box.addView(cutting, LinearLayout.LayoutParams(-1, dp(58)).apply {
            setMargins(dp(18), dp(8), dp(18), dp(8))
        })

        val edit = button("Edit Customer")
        edit.setOnClickListener { showCustomerEditor(c) }
        box.addView(edit, LinearLayout.LayoutParams(-1, dp(58)).apply {
            setMargins(dp(18), dp(8), dp(18), dp(18))
        })

        scroll.addView(box)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun button(text: String): Button {
        return Button(this).apply {
            this.text = text
            textSize = 17f
            setTextColor(white)
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            background = rounded(navy, Color.TRANSPARENT, 0, 14)
            isAllCaps = false
        }
    }

    private fun showEditor(c: Customer) {
        val root = base()
        root.addView(header("Measurements", true))
        val scroll = ScrollView(this)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(18), dp(16), dp(18), dp(24))
        box.setBackgroundColor(card)

        fun addField(label: String, value: String, rtl: Boolean = true): EditText {
            val lab = TextView(this)
            lab.text = label
            lab.textSize = 20f
            lab.textColor = ink
            lab.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            lab.gravity = Gravity.RIGHT
            box.addView(lab, LinearLayout.LayoutParams(-1, dp(36)))
            val e = EditText(this)
            e.setText(value)
            e.textSize = 23f
            e.setTextColor(ink)
            e.gravity = if (rtl) Gravity.RIGHT else Gravity.LEFT
            e.background = rounded(white, stroke, 2, 12)
            e.setPadding(dp(14), 0, dp(14), 0)
            box.addView(e, LinearLayout.LayoutParams(-1, dp(58)).apply { bottomMargin = dp(10) })
            return e
        }

        val m = c.measurements
        val length = addField("لمبائی", m.length, false)
        val width = addField("چوڑائی", m.width, false)
        val teera = addField("تیرہ", m.teera, false)
        val sleeve = addField("بازو", m.sleeve, false)

        val cutLab = TextView(this)
        cutLab.text = "کٹ بین"
        cutLab.textSize = 20f
        cutLab.textColor = ink
        cutLab.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        cutLab.gravity = Gravity.RIGHT
        box.addView(cutLab)

        val cutSpinner = Spinner(this)
        val opts = arrayOf("کٹ بین", "کالر", "بین")
        cutSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opts)
        val current = opts.indexOf(m.cutBenOption).coerceAtLeast(0)
        cutSpinner.setSelection(current)
        box.addView(cutSpinner, LinearLayout.LayoutParams(-1, dp(54)).apply { bottomMargin = dp(10) })
        val cutValue = addField("کٹ بین کی پیمائش", m.cutBen, false)

        val cuff = addField("کف", m.cuff, false)
        val shalwar = addField("شلوار", m.shalwar, false)
        val paincha = addField("پینسہ", m.paincha, false)
        val pocket = addField("پاکٹ", m.pocket, false)

        val damanLab = TextView(this)
        damanLab.text = "دامن"
        damanLab.textSize = 20f
        damanLab.textColor = ink
        damanLab.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        damanLab.gravity = Gravity.RIGHT
        box.addView(damanLab)

        val damanSpinner = Spinner(this)
        val damanOpts = arrayOf("دامن چورس", "دامن گول")
        damanSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, damanOpts)
        damanSpinner.setSelection(damanOpts.indexOf(m.daman).coerceAtLeast(0))
        box.addView(damanSpinner, LinearLayout.LayoutParams(-1, dp(54)).apply { bottomMargin = dp(18) })

        val save = button("Save Measurements")
        save.setOnClickListener {
            m.length = length.text.toString()
            m.width = width.text.toString()
            m.teera = teera.text.toString()
            m.sleeve = sleeve.text.toString()
            m.cutBenOption = cutSpinner.selectedItem.toString()
            m.cutBen = cutValue.text.toString()
            m.cuff = cuff.text.toString()
            m.shalwar = shalwar.text.toString()
            m.paincha = paincha.text.toString()
            m.pocket = pocket.text.toString()
            m.daman = damanSpinner.selectedItem.toString()
            store.save(customers)
            showCutting(c)
        }
        box.addView(save, LinearLayout.LayoutParams(-1, dp(58)))

        scroll.addView(box)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun showCustomerEditor(existing: Customer?) {
        val root = base()
        root.addView(header(if (existing == null) "Add Customer" else "Edit Customer", true))
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(18), dp(18), dp(18), dp(24))
        box.setBackgroundColor(card)

        fun field(hint: String, text: String): EditText {
            val e = EditText(this)
            e.hint = hint
            e.setText(text)
            e.textSize = 18f
            e.setTextColor(ink)
            e.setHintTextColor(muted)
            e.background = rounded(white, stroke, 2, 12)
            e.setPadding(dp(14), 0, dp(14), 0)
            box.addView(e, LinearLayout.LayoutParams(-1, dp(60)).apply { bottomMargin = dp(12) })
            return e
        }

        val name = field("Customer Name (Urdu)", existing?.name ?: "")
        val phone = field("Phone", existing?.phone ?: "")
        val address = field("Address", existing?.address ?: "")
        val notes = field("Notes", existing?.notes ?: "")

        val save = button("Save Customer")
        save.setOnClickListener {
            if (existing == null) {
                val nextId = (customers.maxOfOrNull { it.id } ?: 0) + 1
                customers.add(Customer(nextId, name.text.toString(), phone.text.toString(), address.text.toString(), notes.text.toString()))
            } else {
                existing.name = name.text.toString()
                existing.phone = phone.text.toString()
                existing.address = address.text.toString()
                existing.notes = notes.text.toString()
            }
            store.save(customers)
            showCustomers()
        }
        box.addView(save, LinearLayout.LayoutParams(-1, dp(60)).apply { topMargin = dp(8) })

        val scroll = ScrollView(this)
        scroll.addView(box)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun rounded(fill: Int, border: Int, width: Int, radius: Int): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            setColor(fill)
            if (width > 0) setStroke(dp(width), border)
            cornerRadius = dp(radius).toFloat()
        }
    }

    class Store(private val context: Context) {
        private val prefs = context.getSharedPreferences("tailor_data", Context.MODE_PRIVATE)

        fun save(items: List<Customer>) {
            val arr = JSONArray()
            items.forEach { c ->
                val o = JSONObject()
                o.put("id", c.id)
                o.put("name", c.name)
                o.put("phone", c.phone)
                o.put("address", c.address)
                o.put("notes", c.notes)
                val m = JSONObject()
                m.put("length", c.measurements.length)
                m.put("width", c.measurements.width)
                m.put("teera", c.measurements.teera)
                m.put("sleeve", c.measurements.sleeve)
                m.put("cutBen", c.measurements.cutBen)
                m.put("cutBenOption", c.measurements.cutBenOption)
                m.put("cuff", c.measurements.cuff)
                m.put("shalwar", c.measurements.shalwar)
                m.put("paincha", c.measurements.paincha)
                m.put("pocket", c.measurements.pocket)
                m.put("daman", c.measurements.daman)
                o.put("measurements", m)
                arr.put(o)
            }
            prefs.edit().putString("customers", arr.toString()).apply()
        }

        fun load(): MutableList<Customer> {
            val out = mutableListOf<Customer>()
            val raw = prefs.getString("customers", null) ?: return out
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val mo = o.optJSONObject("measurements") ?: JSONObject()
                val m = Measurements(
                    length = mo.optString("length", "40"),
                    width = mo.optString("width", "18"),
                    teera = mo.optString("teera", "16"),
                    sleeve = mo.optString("sleeve", "24"),
                    cutBen = mo.optString("cutBen", "15"),
                    cuff = mo.optString("cuff", "9"),
                    shalwar = mo.optString("shalwar", "40"),
                    paincha = mo.optString("paincha", "8"),
                    pocket = mo.optString("pocket", "6"),
                    daman = mo.optString("daman", "دامن چورس"),
                    cutBenOption = mo.optString("cutBenOption", "کٹ بین")
                )
                out.add(
                    Customer(
                        o.optLong("id"),
                        o.optString("name"),
                        o.optString("phone"),
                        o.optString("address"),
                        o.optString("notes"),
                        m
                    )
                )
            }
            return out
        }
    }
}
