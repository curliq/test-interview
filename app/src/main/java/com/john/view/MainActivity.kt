package com.john.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.john.databinding.ActivityMainBinding
import com.john.databinding.TransactionViewBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val viewModel = ViewModelProvider(this, HomeViewModelFactory())[HomeViewModel::class.java]

        val adapter = TransactionsAdapter()
        binding.transactions.adapter = adapter
        binding.transactions.layoutManager = LinearLayoutManager(this)

        viewModel.transactions.observeForever {
            it?.let {
                adapter.setData(it)
            }
        }
        viewModel.roundUpTotal.observe(this) {
            binding.cta.text = it
        }
        viewModel.errorMessage.observe(this) {
            if (it != null) {
                binding.error.text = it
            }
            binding.error.visibility = if (it != null) View.VISIBLE else View.GONE
            binding.content.visibility = if (it != null) View.GONE else View.VISIBLE
        }
        viewModel.roundupNotice.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        binding.cta.setOnClickListener {
            viewModel.roundTransactions()
        }
    }
}

class TransactionsAdapter : RecyclerView.Adapter<TransactionsAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(TransactionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        data?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    private var data: List<Transaction>? = null

    fun setData(data: List<Transaction>) {
        // TODO: Use DiffUtil for optimisation
        this.data = data
        notifyDataSetChanged()
    }

    class VH(val binding: TransactionViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction) {
            binding.amount.text = transaction.amount
            binding.merchant.text = transaction.name
        }
    }
}