package ru.job4j.articles.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.articles.model.Article;
import ru.job4j.articles.model.Word;
import ru.job4j.articles.service.generator.ArticleGenerator;
import ru.job4j.articles.store.Store;

import java.util.ArrayList;
import java.util.List;

public class SimpleArticleService implements ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleArticleService.class.getSimpleName());

    private final ArticleGenerator articleGenerator;

    private final int batchSize = 10000;

    private int articleCounter = 0;

    public SimpleArticleService(ArticleGenerator articleGenerator) {
        this.articleGenerator = articleGenerator;
    }

    @Override
    public void generate(Store<Word> wordStore, int count, Store<Article> articleStore) {
        LOGGER.info("Геренация статей в количестве {}", count);
        var words = wordStore.findAll();
        int lastBatchSize = count % batchSize == 0 ? batchSize : count % batchSize;
        int batchesNumber = count / batchSize > 0 ? (int) Math.floor(count / batchSize) : 1;
        for (int i = 0; i < batchesNumber; i++) {
            boolean last = batchesNumber - i == 1 ? true : false;
            int size = last ? lastBatchSize : batchSize;
            List<Article> articles = generateBatch(words, size);
            articles.forEach(articleStore::save);
        }
    }

    private List<Article> generateBatch(List<Word> words, int size) {
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LOGGER.info(String.format("Сгенерирована статья № %s", articleCounter));
            Article article = articleGenerator.generate(words);
            articles.add(article);
            articleCounter++;
        }
        return articles;
    }
}
