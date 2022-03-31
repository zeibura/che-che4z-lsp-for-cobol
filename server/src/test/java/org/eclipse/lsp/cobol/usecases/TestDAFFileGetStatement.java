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

/** Tests the DAF GET FILE statement */
class TestDAFFileGetStatement {

  private static final String TEXT =
      "        IDENTIFICATION DIVISION. \r\n"
          + "        PROGRAM-ID. test1. \r\n"
          + "        DATA DIVISION. \r\n"
          + "        WORKING-STORAGE SECTION. \r\n"
          + "        01 {$*WS-AREA}. \r\n"
          + "           03 {$*AREA-XW1}. \r\n"
          + "             05 {$*DET001-XW1}. \r\n"
          + "               07 FILLER               PIC X(5)    VALUE 'REMBD'. \r\n"
          + "        PROCEDURE DIVISION. \r\n"
          + "            GET FILE {$DET001-XW1} INTO {$DET001-XW1}. \r\n"
          + "            GET FILE {$DET001-XW1} INTO {$DET001-XW1} VOLSER INTO {$DET001-XW1}. \r\n"
          + "            GET FILE 'SDFSE' INTO {$DET001-XW1}. \r\n"
          + "            GET FILE 'SDFSE' INTO {$DET001-XW1} VOLSER INTO {$DET001-XW1}. \r\n"
          // Negative Tests
          // This can't be processed correctly due to the way of dialect matching
          //+ "            GET FILE {.|1} \r\n"
          //+ "            GET FILE {$DET001-XW1} {.|2} \r\n"
          + "            GET FILE {$DET001-XW1} INTO {ABCD|3}. \r\n"
          //+ "            GET FILE {$DET001-XW1} INTO {$DET001-XW1} VOLSER {.|4} \r\n"
          + "            GET FILE {$DET001-XW1} INTO {$DET001-XW1} VOLSER INTO {ABCD|3}. \r\n";

  @Test
  void test() {

    UseCaseEngine.runTest(
        TEXT,
        ImmutableList.of(),
        ImmutableMap.of(
            "1",
            new Diagnostic(
                null,
                "Syntax error on '.' expected {NONNUMERICLITERAL, IDENTIFIER}",
                DiagnosticSeverity.Error,
                SourceInfoLevels.ERROR.getText()),
            "2",
            new Diagnostic(
                null,
                "Syntax error on '.' expected {IN, INTO, OF, '('}",
                DiagnosticSeverity.Error,
                SourceInfoLevels.ERROR.getText()),
            "3",
            new Diagnostic(
                null,
                "Variable ABCD is not defined",
                DiagnosticSeverity.Error,
                SourceInfoLevels.ERROR.getText()),
            "4",
            new Diagnostic(
                null,
                "Syntax error on '.' expected INTO",
                DiagnosticSeverity.Error,
                SourceInfoLevels.ERROR.getText())),
        ImmutableList.of(),
        DialectConfigs.getDaCoAnalysisConfig());
  }
}
