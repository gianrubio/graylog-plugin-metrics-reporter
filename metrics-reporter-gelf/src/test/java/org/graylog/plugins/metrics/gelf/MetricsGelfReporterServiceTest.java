/**
 * This file is part of Graylog Metrics GELF Reporter Plugin.
 *
 * Graylog Metrics GELF Reporter Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog Metrics GELF Reporter Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog Metrics GELF Reporter Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.metrics.gelf;

import com.github.joschi.jadconfig.util.Duration;
import org.graylog.metrics.GelfReporter;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MetricsGelfReporterServiceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private GelfReporter gelfReporter;
    private final MetricsGelfReporterConfiguration configuration = new MetricsGelfReporterConfiguration() {
        @Override
        public boolean isEnabled() {
            return true;
        }
    };

    @Test
    public void serviceStartsJmxReporterIfEnabled() throws Exception {
        assumeTrue(configuration.isEnabled());
        final MetricsGelfReporterService service = new MetricsGelfReporterService(gelfReporter, configuration);
        service.startAsync().awaitRunning();
        final Duration reportInterval = configuration.getReportInterval();
        verify(gelfReporter).start(reportInterval.getQuantity(), reportInterval.getUnit());
    }

    @Test
    public void serviceDoesNotStartJmxReporterIfDisabled() throws Exception {
        final MetricsGelfReporterConfiguration configuration = new MetricsGelfReporterConfiguration();
        assumeFalse(configuration.isEnabled());
        final MetricsGelfReporterService service = new MetricsGelfReporterService(gelfReporter, configuration);
        service.startAsync().awaitRunning();
        final Duration reportInterval = configuration.getReportInterval();
        verify(gelfReporter, never()).start(reportInterval.getQuantity(), reportInterval.getUnit());
    }

    @Test
    public void serviceStopsJmxReporterIfEnabled() throws Exception {
        assumeTrue(configuration.isEnabled());
        final MetricsGelfReporterService service = new MetricsGelfReporterService(gelfReporter, configuration);
        service.startAsync().awaitRunning();
        service.stopAsync().awaitTerminated();
        verify(gelfReporter).stop();
    }

    @Test
    public void serviceDoesNotStopJmxReporterIfDisabled() throws Exception {
        final MetricsGelfReporterConfiguration configuration = new MetricsGelfReporterConfiguration();
        assumeFalse(configuration.isEnabled());
        final MetricsGelfReporterService service = new MetricsGelfReporterService(gelfReporter, configuration);
        service.startAsync().awaitRunning();
        service.stopAsync().awaitTerminated();
        verify(gelfReporter, never()).stop();
    }
}