/*
 * Copyright (c) 2020 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package org.eclipse.lsp.cobol.core.preprocessor.delegates;

import com.google.inject.Inject;
import lombok.NonNull;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.eclipse.lsp.cobol.core.CobolPreprocessor;
import org.eclipse.lsp.cobol.core.CobolPreprocessorLexer;
import org.eclipse.lsp.cobol.core.engine.ThreadInterruptionUtil;
import org.eclipse.lsp.cobol.core.messages.MessageService;
import org.eclipse.lsp.cobol.core.model.ExtendedDocument;
import org.eclipse.lsp.cobol.core.model.ResultWithErrors;
import org.eclipse.lsp.cobol.core.model.SyntaxError;
import org.eclipse.lsp.cobol.core.preprocessor.CopybookHierarchy;
import org.eclipse.lsp.cobol.core.preprocessor.delegates.copybooks.GrammarPreprocessorListener;
import org.eclipse.lsp.cobol.core.preprocessor.delegates.copybooks.GrammarPreprocessorListenerFactory;
import org.eclipse.lsp.cobol.core.preprocessor.delegates.copybooks.ReplacePreProcessorListener;
import org.eclipse.lsp.cobol.core.preprocessor.delegates.copybooks.ReplacingService;
import org.eclipse.lsp.cobol.service.CopybookConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * This class runs pre-processing for COBOL using CobolPreprocessor.g4 grammar file. As a result, it
 * returns an extended document with all the available copybooks included, with their definitions
 * and usages specified, as well as related errors.
 */
public class GrammarPreprocessorImpl implements GrammarPreprocessor {
  private final GrammarPreprocessorListenerFactory listenerFactory;
  private final ReplacingService replacingService;
  private final MessageService messageService;

  @Inject
  public GrammarPreprocessorImpl(
      GrammarPreprocessorListenerFactory listenerFactory,
      ReplacingService replacingService,
      MessageService messageService) {
    this.listenerFactory = listenerFactory;
    this.replacingService = replacingService;
    this.messageService = messageService;
  }

  @NonNull
  @Override
  public ResultWithErrors<ExtendedDocument> buildExtendedDocument(
      @NonNull String uri,
      @NonNull String code,
      @NonNull CopybookConfig copybookConfig,
      @NonNull CopybookHierarchy hierarchy) {
    ThreadInterruptionUtil.checkThreadInterrupted();
    ReplacePreProcessorListener replaceListener = handleReplaceClauses(code, uri, hierarchy);
    List<SyntaxError> errors = new ArrayList<>();
    code = replaceListener.getResult().unwrap(errors::addAll).getText();
    Lexer lexer = new CobolPreprocessorLexer(CharStreams.fromString(code));
    lexer.removeErrorListeners();

    BufferedTokenStream tokens = new CommonTokenStream(lexer);

    CobolPreprocessor parser = new CobolPreprocessor(tokens);
    parser.removeErrorListeners();

    RuleContext startRule = parser.startRule();

    ParseTreeWalker walker = new ParseTreeWalker();
    GrammarPreprocessorListener listener =
        listenerFactory.create(uri, tokens, copybookConfig, hierarchy);
    walker.walk(listener, startRule);
    return new ResultWithErrors<>(listener.getResult().unwrap(errors::addAll), errors);
  }

  private ReplacePreProcessorListener handleReplaceClauses(
      String code, String uri, CopybookHierarchy hierarchy) {
    Lexer lexer = new CobolPreprocessorLexer(CharStreams.fromString(code));
    BufferedTokenStream tokens = new CommonTokenStream(lexer);
    ReplacePreProcessorListener replacePreProcessorListener =
        new ReplacePreProcessorListener(replacingService, messageService, tokens, uri, hierarchy);
    CobolPreprocessor parser = new CobolPreprocessor(tokens);
    CobolPreprocessor.StartRuleContext tree = parser.startRule();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(replacePreProcessorListener, tree);
    return replacePreProcessorListener;
  }
}
