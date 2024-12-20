package com.dna.beyoureyes

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView


class OnboardingFragment : Fragment() {
    companion object {
        private const val ARG_NUMBER = "arg_number"
        fun newInstance(number: Int): OnboardingFragment  {
            val fragment = OnboardingFragment ()
            val args = Bundle()
            args.putInt(ARG_NUMBER, number)
            fragment.arguments = args
            return fragment
        }
    }

    private fun setPartialTextSizeSp(textView: TextView, fullText: String, targetText: String, sizeInSp: Int) {
        val spannableString = SpannableString(fullText)

        val startIndex = fullText.indexOf(targetText)
        val endIndex = startIndex + targetText.length

        if (startIndex >= 0) {
            spannableString.setSpan(
                AbsoluteSizeSpan(sizeInSp, true),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLACK),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.text = spannableString
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        val number = arguments?.getInt(ARG_NUMBER) ?: 0

        val textView = view.findViewById<TextView>(R.id.textView)
        //val imageView = view.findViewById<ImageView>(R.id.imageView)
        val lottieView = view.findViewById<LottieAnimationView>(R.id.lottieView)
        if (number == 1) {
            val fullText = "나만을 위한\n더 쉬운 식품 정보\n\n\n당신의 안식은 시력이 좋지 않은 노인 및\n식품 알레르기가 있는 사용자를 위한 어플입니다"
            val targetText = "나만을 위한\n더 쉬운 식품 정보"
            //imageView.setImageResource(R.drawable.onboarding_image1)
            lottieView.setAnimation(R.raw.onboard_1)
            lottieView.playAnimation()
            setPartialTextSizeSp(textView, fullText, targetText, 24)
        } else if (number == 2) {
            val fullText = "카메라로 읽어주는\n식품 정보 음성 인식 기능\n\n\n카메라로 촬영한 식품 정보의 작은 글씨를 터치하면, \n인공지능이 해당 정보를 음성으로 읽어줍니다."
            val targetText = "카메라로 읽어주는\n식품 정보 음성 인식 기능"
            //imageView.setImageResource(R.drawable.onboarding_image2)
            lottieView.setAnimation(R.raw.onboard_2)
            lottieView.playAnimation()
            setPartialTextSizeSp(textView, fullText, targetText, 24)

        } else if (number == 3) {
            val fullText = "건강한 식사 습관 촉진과\n식품 섭취 관련 문제 최소화\n\n\n건강한 식사 습관을 촉진하고 식품 섭취와 관련된\n문제를 최소화하는 것을 목표로 합니다."
            val targetText = "건강한 식사 습관 촉진과\n식품 섭취 관련 문제 최소화"
            //imageView.setImageResource(R.drawable.onboarding_image3)
            lottieView.setAnimation(R.raw.onboard_3)
            lottieView.playAnimation()
            setPartialTextSizeSp(textView, fullText, targetText, 24)
        }
        return view
    }
}