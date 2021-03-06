package co.omisego.omgshop.pages.checkout

/*
 * OmiseGO
 *
 * Created by Phuchit Sirimongkolsathien on 4/12/2017 AD.
 * Copyright © 2017-2018 OmiseGO. All rights reserved.
 */

import co.omisego.omgshop.base.BaseContract
import java.math.BigDecimal

interface RedeemDialogContract {
    interface View : BaseContract.BaseView {
        fun setTextRedeemAmount(redeem: String)
        fun setTextDiscount(discount: String)
        fun sendDiscountToCheckoutPage(discount: BigDecimal)
    }

    interface Presenter : BaseContract.BasePresenter<View, BaseContract.BaseCaller> {
        fun redeemChanged(value: BigDecimal, symbol: String)
        fun handleClickRedeem()
    }
}