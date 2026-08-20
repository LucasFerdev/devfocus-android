package br.com.devfocus.data.repository

import br.com.devfocus.data.local.dao.QuoteDao
import br.com.devfocus.data.local.entity.QuoteEntity
import br.com.devfocus.data.local.preferences.DevFocusPreferences
import br.com.devfocus.domain.repository.QuoteRepository
import br.com.devfocus.widget.WidgetUpdater
import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class QuoteRepositoryImpl(
    private val context: Context,
    private val quoteDao: QuoteDao,
    private val preferences: DevFocusPreferences
) : QuoteRepository {

    override fun getDailyQuote(): Flow<QuoteEntity?> {
        return preferences.dailyQuoteId.flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                // In a real app we'd want to observe this if it can change
                // but since it's the daily quote, we can just fetch it once it's set
                val quote = quoteDao.getQuoteById(id)
                flowOf(quote)
            }
        }
    }

    override fun getFavoriteQuotes(): Flow<List<QuoteEntity>> = quoteDao.getFavoriteQuotes()

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        quoteDao.updateFavoriteStatus(id, isFavorite)
        WidgetUpdater.update(context)
    }

    override suspend fun refreshDailyQuote() {
        val today = LocalDate.now().toString()
        val lastDate = preferences.dailyQuoteDate.first()

        if (lastDate != today) {
            val count = quoteDao.getQuoteCount()
            if (count > 0) {
                // Simple logic to pick a quote based on date hash to be semi-random but stable
                // Or just random if not set for today
                val allQuotes = quoteDao.getAllQuotes().first()
                if (allQuotes.isNotEmpty()) {
                    val randomQuote = allQuotes.random()
                    preferences.saveDailyQuote(randomQuote.id, today)
                    WidgetUpdater.update(context)
                }
            }
        }
    }

    override suspend fun seedQuotesIfEmpty() {
        if (quoteDao.getQuoteCount() == 0) {
            quoteDao.insertQuotes(initialQuotes.map { QuoteEntity(text = it) })
        }
        refreshDailyQuote()
    }

    private val initialQuotes = listOf(
        "O código que você escreve hoje é o futuro que você constrói amanhã.",
        "A disciplina é a ponte entre metas e realizações.",
        "Você não precisa ser o melhor hoje. Só precisa ser melhor do que ontem.",
        "Pequenos progressos diários resultam em grandes resultados a longo prazo.",
        "A persistência é o caminho do êxito.",
        "Não pare até se orgulhar.",
        "O segredo do sucesso é a constância no objetivo.",
        "Grandes coisas nunca vêm de zonas de conforto.",
        "Foque no processo, o resultado será uma consequência.",
        "Codar é a arte de resolver problemas que você mesmo criou.",
        "A melhor forma de prever o futuro é inventá-lo.",
        "Seja a mudança que você deseja ver no mundo do desenvolvimento.",
        "Menos pressa, mais consistência.",
        "Estudar não é o que você faz, é quem você se torna.",
        "O único lugar onde o sucesso vem antes do trabalho é no dicionário.",
        "A jornada de mil milhas começa com um único commit.",
        "Não é sobre quanto você estuda, mas como você aplica.",
        "Erros são provas de que você está tentando.",
        "Sua única competição é quem você foi ontem.",
        "A mente que se abre a uma nova ideia jamais voltará ao seu tamanho original.",
        "A educação é a arma mais poderosa que você pode usar para mudar o mundo.",
        "O aprendizado é um tesouro que seguirá seu dono em qualquer lugar.",
        "Não limite seus desafios, desafie seus limites.",
        "O que você faz hoje pode melhorar todos os seus amanhãs.",
        "Acredite que você pode e você já está no meio do caminho.",
        "Tudo o que você sempre quis está do outro lado do medo.",
        "O sucesso é a soma de pequenos esforços repetidos dia após dia.",
        "Se você quer algo que nunca teve, precisa fazer algo que nunca fez.",
        "A falha é apenas a oportunidade de começar de novo, desta vez de forma mais inteligente.",
        "Seja mais forte do que sua melhor desculpa.",
        "O entusiasmo é a maior força da alma.",
        "Não importa o quão devagar você vá, desde que você não pare.",
        "A persistência realiza o impossível.",
        "O trabalho duro vence o talento quando o talento não trabalha duro.",
        "Oportunidades não surgem. Você as cria.",
        "Nada é impossível, a própria palavra diz 'eu sou possível'.",
        "Você nunca é velho demais para definir outro objetivo ou sonhar um novo sonho.",
        "A única maneira de fazer um excelente trabalho é amar o que você faz.",
        "Se você pode sonhar, você pode fazer.",
        "O sucesso não é o final, a falha não é fatal: é a coragem de continuar que conta.",
        "Não conte os dias, faça os dias contarem.",
        "Sempre parece impossível até que seja feito.",
        "A motivação é o que faz você começar. O hábito é o que faz você continuar.",
        "Para ser um mestre, você deve estar disposto a ser um aprendiz.",
        "O conhecimento é poder, mas a prática leva à perfeição.",
        "Codar é superpoder. Use-o para o bem.",
        "Um programador é um criador de universos.",
        "A simplicidade é o último grau da sofisticação.",
        "Primeiro resolva o problema, depois escreva o código.",
        "A depuração é o dobro do esforço de escrever o código."
    )
}
