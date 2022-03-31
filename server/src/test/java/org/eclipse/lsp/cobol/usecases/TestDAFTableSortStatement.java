/*
 * Copyright (c) 2022 DAF Trucks NV.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * DAF Trucks NV – implementation of DaCo COBOL statements
 * and DAF development standards
 */
package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.service.delegates.validations.SourceInfoLevels;
import org.eclipse.lsp.cobol.usecases.engine.UseCaseEngine;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.junit.jupiter.api.Test;

/** Tests the DAF ROW Add statement */
class TestDAFTableSortStatement {

  private static final String TEXT =
      "        IDENTIFICATION DIVISION. \r\n"
          + "        PROGRAM-ID. test1. \r\n"
          + "        DATA DIVISION. \r\n"
          + "        WORKING-STORAGE SECTION. \r\n"
          + "        01 {$*WS-AREA}. \r\n"
          + "           03 {$*AREA-XW1}. \r\n"
          + "             05 {$*A}. \r\n"
          + "               07 FILLER               PIC X(5)    VALUE 'REMBD'. \r\n"
          + "        PROCEDURE DIVISION. \r\n"
          + "            {_SORT TABLE {$A} TO {$A} LENGTH {$A} ASCENDING|unsupported_}. \r\n"
          + "            {_SORT TABLE {$A} TO {$A} LENGTH {$A} DESCENDING|unsupported_}. \r\n"
          + "            {_SORT TABLE {$A} TO {$A} LENGTH 2 ASCENDING|unsupported_}. \r\n"
          + "            {_SORT TABLE {$A} TO {$A} LENGTH 2 DESCENDING|unsupported_}. \r\n";

  @Test
  void test() {

    UseCaseEngine.runTest(
        TEXT,
        ImmutableList.of(),
        ImmutableMap.of(
            "unsupported",
            new Diagnostic(
                null,
                "The code block is deprecated and not supported",
                DiagnosticSeverity.Warning,
                SourceInfoLevels.WARNING.getText())),
        ImmutableList.of(),
        IdmsBase.getAnalysisConfig());
  }
}