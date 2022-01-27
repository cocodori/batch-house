package com.fastcampus.housebatch.job.apt

import com.fastcampus.housebatch.core.repository.LawdRepository
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.repeat.RepeatStatus

/**
 * ExecutionContext에 저장할 데이터
 * 1. guLawdCdList - 구 코드 리스트
 * 2. guLawdCd - 구 코드 - 다음 스텝에서 활용할 값
 * 3. itemCount - 남아있는 아이템의 수
 */
class GuLawdTasklet(
    private val lawdRepository: LawdRepository
) : Tasklet {

    companion object {
        private const val KEY_GU_LAWD_CD_LIST = "guLawdCdList"
        private const val KEY_GU_LAWD_CD = "guLawdCd"
        private const val KEY_ITEM_COUNT = "itemCount"
        private const val CONTINUABLE = "CONTINUABLE"
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val executionContext = getExecutionContext(chunkContext)
        val guLawdCdList: List<String> = initLawdCdList(executionContext)
        var itemCount = initItemCount(executionContext)

        if (itemCount == 0) {
            contribution.exitStatus = ExitStatus.COMPLETED
            return RepeatStatus.FINISHED
        }

        executionContext.put(KEY_GU_LAWD_CD, guLawdCdList[--itemCount])
        executionContext.putInt(KEY_ITEM_COUNT, itemCount)

        contribution.exitStatus = ExitStatus(CONTINUABLE)

        return RepeatStatus.FINISHED
    }

    private fun initItemCount(
        executionContext: ExecutionContext,
    ): Int {
        return executionContext.getInt(KEY_ITEM_COUNT)
    }

    private fun initLawdCdList(executionContext: ExecutionContext) =
        if (executionContext.containsKey(KEY_GU_LAWD_CD_LIST)) {
            executionContext.get(KEY_GU_LAWD_CD_LIST) as List<String>
        } else {
            val guLawdCdList = lawdRepository.findDistinctGuLawdCd()
            executionContext.put(KEY_GU_LAWD_CD_LIST, guLawdCdList)
            executionContext.putInt(KEY_ITEM_COUNT, guLawdCdList.size)
            guLawdCdList
        }

    private fun getExecutionContext(chunkContext: ChunkContext): ExecutionContext {
        val stepExecution = chunkContext.stepContext.stepExecution
        val executionContext = stepExecution.jobExecution.executionContext
        return executionContext
    }
}