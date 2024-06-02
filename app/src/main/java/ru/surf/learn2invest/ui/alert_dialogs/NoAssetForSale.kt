//package ru.surf.learn2invest.ui.alert_dialogs
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import ru.surf.learn2invest.databinding.NoAssetForSaleDialogBinding
//import ru.surf.learn2invest.ui.alert_dialogs.parent.CustomAlertDialog
//
//class NoAssetForSale(
//    context: Context
//) : CustomAlertDialog(context = context) {
//
//    private var binding =
//        NoAssetForSaleDialogBinding.inflate(LayoutInflater.from(context))
//
//    override fun setCancelable(): Boolean {
//        return true
//    }
//
//    override fun initListeners() {
//        binding.buttonExitNoAssetForSaleDialog.setOnClickListener {
//            cancel()
//        }
//    }
//
//    override fun getDialogView(): View {
//        return binding.root
//    }
//
//}