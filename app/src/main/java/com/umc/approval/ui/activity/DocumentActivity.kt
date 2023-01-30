package com.umc.approval.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.umc.approval.databinding.ActivityDocumentBinding
import com.umc.approval.ui.adapter.document_comment_activity.DocumentCommentAdapter
import com.umc.approval.ui.adapter.document_comment_activity.DocumentCommentItem
import com.umc.approval.ui.viewmodel.approval.DocumentViewModel
import com.umc.approval.ui.viewmodel.comment.CommentViewModel
import com.umc.approval.R
import com.umc.approval.data.dto.approval.post.AgreePostDto
import com.umc.approval.ui.adapter.document_comment_activity.DocumentCommentItem2
import com.umc.approval.ui.fragment.document.ApproveDialog
import com.umc.approval.ui.fragment.document.RefuseDialog

class DocumentActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDocumentBinding

    /**Approval view model*/
    private val viewModel by viewModels<DocumentViewModel>()
    private val commentViewModel by viewModels<CommentViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDocumentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //댓글
        setComment()

        //다른 곳으로 이동하는 서비스
        move_to_other()

        approve_or_reject()

        //서류가 들어왔을때 View 구성
        live_data()
    }

    //결재 또는 반려 버튼 클릭 로직
    private fun approve_or_reject() {
        //반려버튼 클릭시 엑세스 토큰으로 로그인 상태 확인 후, 로그인 아니면 로그인 과정 거치기
        //투표를 하지 않았을때만 로직 진행
        binding.approveButton.setOnClickListener {
            if (viewModel.accessToken.value == true) {
                if (viewModel.document.value!!.isVoted == 0) {
                    val dialog = ApproveDialog()
                    dialog.show(supportFragmentManager, dialog.tag)
                }
            } else {
                Toast.makeText(this, "로그인 과정이 필요합니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        //반려버튼 클릭시 엑세스 토큰으로 로그인 상태 확인 후, 로그인 아니면 로그인 과정 거치기
        //투표를 하지 않았을때만 로직 진행
        binding.refuseButton.setOnClickListener {
            if (viewModel.accessToken.value == true) {
                if (viewModel.document.value!!.isVoted == 0) {
                    val dialog = RefuseDialog()
                    dialog.show(supportFragmentManager, dialog.tag)
                }
            } else {
                Toast.makeText(this, "로그인 과정이 필요합니다", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //결재서류 라이브 데이터
    private fun live_data() {
        viewModel.document.observe(this) {

            binding.profile.load(it.profileImage)
            binding.name.text = it.nickname
            binding.title.text = it.title
            binding.content.text = it.content
            binding.documentCommentPostLikes.text = "좋아요 " + it.likedCount.toString()
            binding.documentCommentPostViews.text = "조회수 " + it.view.toString()
            binding.documentCommentPostComments.text = "댓글 " + it.commentCount.toString()
            binding.documentCommentPostTime.text = it.datetime
            binding.approveButton.text = "승인" + it.approveCount
            binding.refuseButton.text = "승인" + it.rejectCount

            //링크 처리
            binding.openGraphImage.load(it.link.image)
            binding.openGraphText.text = it.link.title
            binding.openGraphUrl.text = it.link.url

            //이미지 처리
            binding.image1.load(it.imageUrl.get(0))
            binding.image2.load(it.imageUrl.get(1))
            binding.image3.load(it.imageUrl.get(2))

            //투표를 이미 했으면 색 세팅
            if (viewModel.document.value!!.isVoted == 1) {
                binding.approveButtonIcon.setImageResource(R.drawable.document_approval_icon_selected)
                binding.approveButton.setTextColor(Color.parseColor("#141414"))
            } else if (viewModel.document.value!!.isVoted == 2) {
                binding.refuseButtonIcon.setImageResource(R.drawable.document_refusal_icon_selected)
                binding.refuseButton.setTextColor(Color.parseColor("#141414"))
            }
        }
    }

    //뷰 이동 로직
    private fun move_to_other() {
        //좋아요 액티비티로 이동
        binding.documentCommentPostLikes.setOnClickListener {
            startActivity(Intent(this, LikeActivity::class.java))
        }

        binding.cancel.setOnClickListener {
            finish()
        }
    }

    //시작시 로그인 상태 검증 및 서류 정보 가지고 오는 로직
    override fun onStart() {
        super.onStart()

        /**AccessToken 확인해서 로그인 상태인지 아닌지 확인*/
        viewModel.checkAccessToken()

        val documentId = intent.getStringExtra("documentId")

        viewModel.get_document_detail(documentId.toString())
    }

    //뷰 재시작시 로그인 상태 검증 및 서류 정보 가지고 오는 로직
    override fun onResume() {
        super.onResume()

        /**AccessToken 확인해서 로그인 상태인지 아닌지 확인*/
        viewModel.checkAccessToken()

        val documentId = intent.getStringExtra("documentId")

        viewModel.get_document_detail(documentId.toString())
    }

    private fun setComment() {
        binding.documentCommentRecyclerview.layoutManager = LinearLayoutManager(this)
        val itemList = ArrayList<DocumentCommentItem2>()
        for (i in 1..20) {
            val itemList2 = ArrayList<DocumentCommentItem>()
            val itemList3 = ArrayList<DocumentCommentItem>()
            itemList2.add(DocumentCommentItem("김부장", "댓글 내용 텍스트입니다 /nabcdefghijklmnopqrstuvwxyz0123456789", "12/22 1 시간 전", 50))
            itemList3.add(DocumentCommentItem("이차장", "댓글 내용 텍스트입니다 /nabcdefghijklmnopqrstuvwxyz0123456789", "12/22 1 시간 전", 50))
            itemList3.add(DocumentCommentItem("이차장", "댓글 내용 텍스트입니다 /nabcdefghijklmnopqrstuvwxyz0123456789", "12/22 1 시간 전", 50))
            itemList3.add(DocumentCommentItem("이차장", "댓글 내용 텍스트입니다 /nabcdefghijklmnopqrstuvwxyz0123456789", "12/22 1 시간 전", 50))
            itemList.add(
                DocumentCommentItem2(DocumentCommentItem2.TYPE_1, itemList2)
            )
            itemList.add(
                DocumentCommentItem2(DocumentCommentItem2.TYPE_2, itemList3)
            )
        }
        val documentCommentAdapter = DocumentCommentAdapter(itemList)
        documentCommentAdapter.notifyDataSetChanged()

        binding.documentCommentRecyclerview.adapter = documentCommentAdapter
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        /*
        Log.d("before", binding.row1Content.height.toString())
        Log.d("value", binding.row2Content.height.toString())
        binding.row2Content.measuredHeightAndState
        Log.d("value2", (binding.line2.y - binding.line1.y).toString())
        binding.row1Content.layoutParams = ConstraintLayout.LayoutParams(binding.row1Content.width, binding.row2Content.measuredHeight)
            //binding.row2Content.height + 250)
        setContentView(binding.root)
        Log.d("after", binding.row1Content.height.toString())

         */
        binding.rowBackgroundImg.minimumHeight = binding.documentCommentPost.measuredHeight
        setContentView(binding.root)
    }

    //승인 시 승인 Api
    fun changeApproveButton() {
        binding.approveButtonIcon.setImageResource(R.drawable.document_approval_icon_selected)
        binding.approveButton.setTextColor(Color.parseColor("#141414"))

        viewModel.agree_document(viewModel.document.value!!.documentId.toString(), AgreePostDto(true))
    }

    //반려 시 반려 Api
    fun changeRefuseButton(){
        binding.refuseButtonIcon.setImageResource(R.drawable.document_refusal_icon_selected)
        binding.refuseButton.setTextColor(Color.parseColor("#141414"))

        viewModel.agree_document(viewModel.document.value!!.documentId.toString(), AgreePostDto(false))
    }
}