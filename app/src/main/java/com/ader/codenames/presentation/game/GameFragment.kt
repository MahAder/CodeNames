package com.ader.codenames.presentation.game

import android.R.attr
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ader.codenames.R
import com.ader.codenames.databinding.FragmentGameBinding
import com.ader.codenames.domain.interactor.game.model.GameStatus
import com.ader.codenames.domain.utils.GameConstants
import com.ader.codenames.presentation.model.GameStatusUI
import com.ader.codenames.presentation.model.GameStatusUIModel
import com.ader.codenames.presentation.start.MasterKeyDialogFragment
import com.ader.eventbudget20.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.Intent

import android.R.attr.bitmap
import android.provider.MediaStore
import android.graphics.Bitmap.CompressFormat

import android.R.attr.bitmap
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class GameFragment: BaseFragment<GameViewModel>() {
    private lateinit var binding: FragmentGameBinding

    override val viewModel: GameViewModel by viewModels()

    private val wordsAdapter = GameWordsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWordList()
        viewModel.wordsLiveData.observe(viewLifecycleOwner, Observer {
            wordsAdapter.setData(it)
        })

        viewModel.gameStatusLiveData.observe(viewLifecycleOwner, {
            handleGameStatusUpdate(it)
        })

        viewModel.gameFinishedLiveData.observe(viewLifecycleOwner, {
            findNavController().popBackStack()
        })

        binding.endMoveTv.setOnClickListener {
            viewModel.endMove()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.game_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.turnUpCards){
            turnUpCards()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun turnUpCards(){
        if(viewModel.masterMode){
            viewModel.hideClosedCardsColor()
        } else {
            val dialog = MasterKeyDialogFragment()
            dialog.dialogEventListener = object : MasterKeyDialogFragment.DialogEventListener{
                override fun onApproveClick(key: String) {
                    viewModel.showAllCardsColor(key)
                    dialog.dismiss()
                }
            }
            dialog.show(childFragmentManager, "")
        }
    }

    private fun initWordList(){
        val spanCount = resources.getInteger(R.integer.word_span_count)
        binding.wordsRecycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
        wordsAdapter.wordClickListener = object : GameWordsAdapter.WordClickListener {
            override fun onClosedWordClick(position: Int) {
                val updatedWord = viewModel.openWord(position)
                wordsAdapter.updateWord(updatedWord, position)
            }
        }
        binding.wordsRecycler.adapter = wordsAdapter
    }

    private fun showMoveTeamSelector(teamColor: GameConstants.TeamColor){
        when(teamColor){
            GameConstants.TeamColor.RED -> {
                binding.blueTeamSelector.visibility = View.GONE
                binding.redTeamSelector.visibility = View.VISIBLE
            }
            GameConstants.TeamColor.BLUE -> {
                binding.blueTeamSelector.visibility = View.VISIBLE
                binding.redTeamSelector.visibility = View.GONE
            }
        }
    }

    private fun handleGameStatusUpdate(gameStatusUIModel: GameStatusUIModel){
        binding.redWordsLeft.text = gameStatusUIModel.redTeamWordsLeftCount.toString()
        binding.blueWordsLeft.text = gameStatusUIModel.blueTeamWordsLeftCount.toString()

        showMoveTeamSelector(gameStatusUIModel.nextMove)

        val status = when(gameStatusUIModel.gameStatus){
            GameStatusUI.NEXT_MOVE -> {
                showNextMoveDialog(gameStatusUIModel.nextMove)
                "Next move"
            }
            GameStatusUI.WORD_CORRECT -> "word correct"
            GameStatusUI.RED_TEAM_WIN -> {
                showWinDialog(GameConstants.TeamColor.RED)
                "Red team win"
            }
            GameStatusUI.BLUE_TEAM_WIN -> {
                showWinDialog(GameConstants.TeamColor.BLUE)
                "blue team win"
            }
            GameStatusUI.DEATH_RED_TEAM_WIN -> {
                showDeathWinDialog(GameConstants.TeamColor.RED)
                "death red team win"
            }
            GameStatusUI.DEATH_BLUE_TEAM_WIN -> {
                showDeathWinDialog(GameConstants.TeamColor.BLUE)
                "death blue team win"
            }
            GameStatusUI.START_GAME -> {
                showStartGameUI(gameStatusUIModel)
                ""
            }
        }
    }

    private fun showStartGameUI(gameStatusUIModel: GameStatusUIModel){
        binding.startGame.visibility = View.VISIBLE
        binding.shareWithMaster.visibility = View.VISIBLE
        binding.redTeamSelector.visibility = View.GONE
        binding.blueTeamSelector.visibility = View.GONE
        binding.redWordsLeft.visibility = View.GONE
        binding.blueWordsLeft.visibility = View.GONE
        binding.endMoveTv.visibility = View.GONE

        binding.startGame.setOnClickListener {
            viewModel.hideClosedCardsColor()
            showGameUI(gameStatusUIModel)
            showNextMoveDialog(gameStatusUIModel.nextMove)
        }

        binding.shareWithMaster.setOnClickListener {
            shareWords()
        }
    }

    private fun showGameUI(gameStatusUIModel: GameStatusUIModel){
        binding.startGame.visibility = View.GONE
        binding.shareWithMaster.visibility = View.GONE
        binding.redWordsLeft.visibility = View.VISIBLE
        binding.blueWordsLeft.visibility = View.VISIBLE
        binding.endMoveTv.visibility = View.VISIBLE
        showMoveTeamSelector(gameStatusUIModel.nextMove)
    }

    private fun shareWords(){
        val v = binding.wordsRecycler
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)

        val file = File(requireContext().cacheDir, "words" + ".png")
        val fOut = FileOutputStream(file)
        b.compress(CompressFormat.PNG, 100, fOut)
        fOut.flush()
        fOut.close()
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName+".fileprovider", file)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/png"
        startActivity(intent)
    }

    private fun showWinDialog(winTeam: GameConstants.TeamColor){
        val text = when(winTeam){
            GameConstants.TeamColor.RED -> getString(R.string.red_team_win)
            GameConstants.TeamColor.BLUE -> getString(R.string.blue_team_win)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(text)
            .setCancelable(false)
            .setPositiveButton(R.string.complete_game
            ) { p0, p1 ->
                viewModel.finishGame()
            }
            .create().show()
    }

    private fun showDeathWinDialog(winTeam: GameConstants.TeamColor){
        val text = when(winTeam){
            GameConstants.TeamColor.RED -> getString(R.string.death_red_team_win)
            GameConstants.TeamColor.BLUE -> getString(R.string.death_blue_team_win)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(text)
            .setCancelable(true)
            .setPositiveButton(R.string.complete_game
            ) { p0, p1 ->
                viewModel.finishGame()
            }
            .setNegativeButton(R.string.continue_game){_, _ ->

            }
            .create().show()
    }

    private fun showNextMoveDialog(moveTeam : GameConstants.TeamColor){
        val text = when(moveTeam){
            GameConstants.TeamColor.RED -> getString(R.string.red_team_move)
            GameConstants.TeamColor.BLUE -> getString(R.string.blue_team_move)
        }

        AlertDialog.Builder(requireContext())
            .setMessage(text)
            .setCancelable(true)
            .create().show()
    }

}