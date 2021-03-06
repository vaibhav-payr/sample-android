package co.omisego.omgshop.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import co.omisego.omgshop.R
import co.omisego.omgshop.helpers.Preference
import co.omisego.omgshop.network.ClientProvider
import co.omisego.omisego.custom.OMGCallback
import co.omisego.omisego.model.APIError
import co.omisego.omisego.model.OMGResponse
import co.omisego.omisego.model.Token
import co.omisego.omisego.model.WalletList
import kotlinx.android.synthetic.main.layout_spinner_field.view.*

class SpinnerField : ConstraintLayout, AdapterView.OnItemSelectedListener {

    private lateinit var adapter: ArrayAdapter<String>
    private var spinner: Spinner? = null
    private var tokens = arrayOf<Token>()
        set(value) {
            field = value
            spinnerItems = value.map { it.symbol }.toMutableList()
        }

    private var spinnerItems = mutableListOf<String>()
        set(value) {
            field = value
            if (adapter.isEmpty)
                adapter.addAll(value)
        }

    var title: String = ""
        set(value) {
            field = value
            tvTitle.text = value
        }
    var subTitle: String = ""
        set(value) {
            field = value
            tvSubTitle.text = value
        }

    lateinit var selectedToken: Token
    var selectionTokenListener: OnSelectionTokenListener? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val rootView = inflate(context, R.layout.layout_spinner_field, this)
        spinner = rootView.findViewById(R.id.spinner)

        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        spinner?.adapter = adapter
        spinner?.onItemSelectedListener = this

        attrs?.apply()

        if (!this.isInEditMode)
            loadSpinnerWithTokens()
    }

    fun selectToken(token: Token) {
        selectedToken = token
    }

    fun isTokenAvailable() = ::selectedToken.isInitialized

    private fun loadSpinnerWithTokens() {
        val authToken = Preference.loadCredential().omisegoAuthenticationToken
        ClientProvider.provideOMGClient(authToken).client.getWallets().enqueue(object : OMGCallback<WalletList> {
            override fun fail(response: OMGResponse<APIError>) {
                spinnerItems = arrayListOf("Cannot load tokens")
            }

            override fun success(response: OMGResponse<WalletList>) {
                tokens = response.data.data[0].balances.map { it.token }.toTypedArray()
                if (::selectedToken.isInitialized) {
                    val index = tokens.indexOf(selectedToken)
                    spinner?.setSelection(index)
                }
            }
        })
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (tokens.isNotEmpty()) {
            selectedToken = tokens[position]
            selectionTokenListener?.onItemSelected(tokens[position])
        }
    }

    private fun AttributeSet?.apply() {
        val attrs = context.theme.obtainStyledAttributes(
            this,
            R.styleable.SpinnerField,
            0, 0)

        try {
            title = attrs.getString(R.styleable.SpinnerField_spinTitle) ?: title
            subTitle = attrs.getString(R.styleable.SpinnerField_spinSubTitle) ?: subTitle
        } finally {
            attrs.recycle()
        }
    }

    interface OnSelectionTokenListener {
        fun onItemSelected(token: Token)
    }
}
